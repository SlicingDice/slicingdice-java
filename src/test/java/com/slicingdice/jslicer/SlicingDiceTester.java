package com.slicingdice.jslicer;

import com.slicingdice.jslicer.core.Requester;
import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SlicingDiceTester {

    private SlicingDice client;
    private boolean _verbose = false;
    // Translation table for fields with timestamp
    private JSONObject _fieldTranslation;

    private long _sleepTime;  // seconds
    private String _path;  // Directory containing examples to test
    private String _extension;  // Examples file format

    public int _numSuccesses;
    public int _numFails;
    public ArrayList<Object> _failedTests;

    public SlicingDiceTester(String apiKey){
        this.client = new SlicingDice(apiKey);
        this.loadConfigTest();
    }

    public SlicingDiceTester(String apiKey, boolean verbose){
        this.client = new SlicingDice(apiKey);
        this._verbose = verbose;
        this.loadConfigTest();
    }

    private void loadConfigTest(){
        this._sleepTime = 5;
        this._path = "src/test/java/com/slicingdice/jslicer/examples/";
        this._extension = ".json";
        this._numSuccesses = 0;
        this._numFails = 0;
        this._failedTests = new ArrayList<Object>();
    }

    public void runTests(String queryType){
        JSONArray testData = this.loadTestData(queryType);
        int numTests = testData.length();

        for(int i = 0; i < numTests; i++) {
            JSONObject test = (JSONObject) testData.get(i);
            this.emptyFieldTranslation();

            System.out.println(String.format("(%1$d/%2$d) Executing test \"%3$s\"", i + 1, numTests, test.getString("name")));
            if (test.has("description")){
                System.out.println(String.format("  Description: %s", test.get("description")));
            }

            System.out.println(String.format("  Query type: %s", queryType));
            JSONObject result = null;
            try{
                this.createFields(test);
                this.indexData(test);
                result = this.executeQuery(queryType, test);
            } catch(SlicingDiceException e){
                result =  new JSONObject()
                        .put("result", new JSONObject()
                            .put("error", e.toString()));
            } catch(Exception e){
                result =  new JSONObject()
                        .put("result", new JSONObject()
                                .put("error", e.toString()));
            }

            this.compareResult(test, result);
        }
    }

    private void emptyFieldTranslation(){
        this._fieldTranslation = new JSONObject();
    }
    private JSONArray loadTestData(String queryType){
        String fle = new File(this._path + queryType + this._extension).getAbsolutePath();
        String content = null;
        try {
            content = new Scanner(new File(fle)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        return new JSONArray(content);
    }

    private void createFields(JSONObject test){
        JSONArray fields = test.getJSONArray("fields");
        boolean isSingular = fields.length() == 1;
        String fieldOrFields = null;
        if(isSingular){
            fieldOrFields = "field";
        } else {
            fieldOrFields = "fields";
        }
        System.out.println(String.format("  Creating %1$d %2$s", fields.length(), fieldOrFields));

        for(Object field : fields){
            JSONObject fieldDict = (JSONObject)field;
            this.addTimestampToFieldName(fieldDict);
            try {
                this.client.createField(fieldDict, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(this._verbose){
                System.out.println(String.format("    - %s", fieldDict.getString("api-name")));
            }
        }
    }

    private void addTimestampToFieldName(JSONObject field){
        String oldName = String.format("\"%s\"", field.getString("api-name"));

        String timestamp = this.getTimestamp();
        field.put("name", field.get("name") + timestamp);
        field.put("api-name", field.get("api-name") + timestamp);

        String newName = String.format("\"%s\"", field.getString("api-name"));
        this._fieldTranslation.put(oldName, newName);
    }

    private String getTimestamp(){
        Long currentTime = System.currentTimeMillis() * 10;
        return currentTime.toString();
    }

    private void indexData(JSONObject test){
        JSONObject index = test.getJSONObject("index");
        boolean isSingular = index.length() == 1;
        String entityOrEntities = null;
        if(isSingular){
            entityOrEntities = "entity";
        } else {
            entityOrEntities = "entities";
        }
        System.out.println(String.format("  Indexing %1$d %2$s", index.length(), entityOrEntities));

        JSONObject indexData = this.translateFieldNames(index);

        try {
            this.client.index(indexData, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Wait a few seconds so the data can be indexed by SlicingDice
            TimeUnit.SECONDS.sleep(this._sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JSONObject translateFieldNames(JSONObject data){
        String dataString = data.toString();

        int counter = 0;
        for (Object oldName : this._fieldTranslation.keySet()) {
            String oldNameStr = (String)oldName;
            String newName = this._fieldTranslation.getString(oldNameStr);

            dataString = dataString.replaceAll(oldNameStr, newName);
        }
        return new JSONObject(dataString);
    }

    private JSONObject executeQuery(String queryType, JSONObject test) throws IOException {
        JSONObject queryData = this.translateFieldNames(test.getJSONObject("query"));

        System.out.println("  Querying");

        if (this._verbose){
            System.out.println(String.format("    - %s", queryData));
        }

        JSONObject result = null;
        if (queryType.equals("count_entity")){
            result = this.client.countEntity(queryData, true);
        } else if (queryType.equals("count_event")){
            result = this.client.countEvent(queryData, true);
        } else if (queryType.equals("top_values")){
            result = this.client.topValues(queryData, true);
        } else if (queryType.equals("aggregation")){
            result = this.client.aggregation(queryData, true);
        } else if (queryType.equals("result")){
            result = this.client.result(queryData, true);
        } else if (queryType.equals("score")){
            result = this.client.score(queryData, true);
        }

        return result;
    }

    private void compareResult(JSONObject test, JSONObject result){
        JSONObject testExpected = test.getJSONObject("expected");
        JSONObject expected = this.translateFieldNames(test.getJSONObject("expected"));

        for (Object key : testExpected.keySet()) {
            String keyStr = (String)key;
            Object value = testExpected.get(keyStr);

            if (value.toString().equals("ignore")){
                continue;
            }

            if(!expected.getJSONObject(keyStr).toString().equals(result.getJSONObject(keyStr).toString())) {
                this._numFails += 1;
                this._failedTests.add(test.getString("name"));

                System.out.println(String.format("  Expected: \"%1$s\": %2$s", keyStr, expected.getJSONObject(keyStr).toString()));
                System.out.println(String.format("  Result: \"%1$s\": %2$s", keyStr, result.getJSONObject(keyStr).toString()));
                System.out.println("  Status: Failed\n");
                return;
            }

            this._numSuccesses += 1;
            System.out.println("  Status: Passed\n");
        }
    }
}
