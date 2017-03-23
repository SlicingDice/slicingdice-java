package com.slicingdice.jslicer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

public class SlicingDiceTester {

    // The SlicingDice client
    private final SlicingDice client;

    private boolean verbose = false;

    // Translation table for fields with timestamp
    private JSONObject fieldTranslation;

    // Sleep time in seconds
    private long sleepTime;

    // Directory containing examples to test
    private String path;

    // Examples file format
    private String fileExtension;

    public int numberOfSuccesses;

    public int numberOfFails;

    public ArrayList<Object> failedTests;

    public SlicingDiceTester(final String apiKey){
        this.client = new SlicingDice(apiKey, true);
        this.loadConfigTest();
    }

    public SlicingDiceTester(final String apiKey, final boolean verbose){
        this.client = new SlicingDice(apiKey, true);
        this.verbose = verbose;
        this.loadConfigTest();
    }

    private void loadConfigTest(){
        this.sleepTime = 10;
        this.path = "src/test/java/com/slicingdice/jslicer/examples/";
        this.fileExtension = ".json";
        this.numberOfSuccesses = 0;
        this.numberOfFails = 0;
        this.failedTests = new ArrayList<>();
    }

    /**
     * Run tests
     * @param queryType the query type
     */
    public void runTests(final String queryType){
        final JSONArray testData = this.loadTestData(queryType);
        final int numberOfTests = testData.length();

        for(int i = 0; i < numberOfTests; i++) {
            final JSONObject testObject = (JSONObject) testData.get(i);
            this.emptyFieldTranslation();

            System.out.println(String.format("(%1$d/%2$d) Executing test \"%3$s\"", i + 1,
                    numberOfTests, testObject.getString("name")));

            if (testObject.has("description")){
                System.out.println(String.format("\tDescription: %s",
                        testObject.get("description")));
            }

            System.out.println(String.format("\tQuery type: %s", queryType));
            JSONObject result = null;
            try{
                this.createFields(testObject);
                this.indexData(testObject);
                result = this.executeQuery(queryType, testObject);
            } catch(Exception e){
                result =  new JSONObject()
                        .put("result", new JSONObject()
                        .put("error", e.toString()));
            }

            try {
                this.compareResult(testObject, queryType, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void emptyFieldTranslation(){
        this.fieldTranslation = new JSONObject();
    }

    /**
     * Load test data from examples files
     *
     * @param queryType the query type
     * @return JSONArray with test data
     */
    private JSONArray loadTestData(String queryType){
        final String file = new File(this.path + queryType + this.fileExtension).getAbsolutePath();
        String content = null;
        try {
            content = new Scanner(new File(file)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new JSONArray(content);
    }

    /**
     * Create fields on SlicingDice API
     * @param fieldObject the field object to create
     */
    private void createFields(final JSONObject fieldObject){
        final JSONArray fields = fieldObject.getJSONArray("fields");
        final boolean isSingular = fields.length() == 1;
        String fieldOrFields = null;

        if(isSingular){
            fieldOrFields = "field";
        } else {
            fieldOrFields = "fields";
        }

        System.out.println(String.format("\tCreating %1$d %2$s", fields.length(), fieldOrFields));

        for(final Object field : fields){
            final JSONObject fieldDict = (JSONObject) field;
            this.addTimestampToFieldName(fieldDict);
            // call client command to create fields
            try {
                this.client.createField(fieldDict);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(this.verbose){
                System.out.println(String.format("\t\t- %s", fieldDict.getString("api-name")));
            }
        }
    }

    /**
     * Add timestamp to field name
     * @param field the field to put timestamp
     */
    private void addTimestampToFieldName(final JSONObject field){
        final String oldName = String.format("\"%s\"", field.getString("api-name"));

        final String timestamp = this.getTimestamp();
        field.put("name", field.get("name") + timestamp);
        field.put("api-name", field.get("api-name") + timestamp);

        final String newName = String.format("\"%s\"", field.getString("api-name"));
        this.fieldTranslation.put(oldName, newName);
    }

    /**
     * Get actual timestamp
     * @return timestamp converted to string
     */
    private String getTimestamp(){
        final Long currentTime = System.currentTimeMillis() * 10;
        return currentTime.toString();
    }

    /**
     * Index data to SlicingDice API
     * @param indexObject the index object to index on SlicingDice
     */
    private void indexData(JSONObject indexObject){
        final JSONObject index = indexObject.getJSONObject("index");
        final boolean isSingular = index.length() == 1;
        String entityOrEntities = null;
        if(isSingular){
            entityOrEntities = "entity";
        } else {
            entityOrEntities = "entities";
        }
        System.out.println(String.format("\tIndexing %1$d %2$s", index.length(), entityOrEntities));

        final JSONObject indexData = this.translateFieldNames(index);

        // call client command to index data
        try {
            this.client.index(indexData, true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while processing your query on SlicingDice");
        }

        try {
            // Wait a few seconds so the data can be indexed by SlicingDice
            TimeUnit.SECONDS.sleep(this.sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("An error occurred while processing your query on SlicingDice");
        }
    }

    /**
     * Translate field name to use timestamp
     * @param field the field to translate
     * @return the new field translated
     */
    private JSONObject translateFieldNames(final JSONObject field) {
        String dataString = field.toString();

        for (final Object oldName : this.fieldTranslation.keySet()) {
            final String oldNameStr = (String)oldName;
            final String newName = this.fieldTranslation.getString(oldNameStr);

            dataString = dataString.replaceAll(oldNameStr, newName);
        }
        return new JSONObject(dataString);
    }

    /**
     * Execute query
     * @param queryType the type of the query
     * @param query the query to send to SlicingDice API
     * @return the result of the query
     * @throws IOException
     */
    private JSONObject executeQuery(final String queryType, final JSONObject query)
            throws IOException {
        final JSONObject queryData = this.translateFieldNames(query.getJSONObject("query"));

        System.out.println("\tQuerying");

        if (this.verbose){
            System.out.println(String.format("\t\t- %s", queryData));
        }

        JSONObject result = null;
        // call client command to make a query
        if (queryType.equals("count_entity")){
            result = this.client.countEntity(queryData);
        } else if (queryType.equals("count_event")){
            result = this.client.countEvent(queryData);
        } else if (queryType.equals("top_values")){
            result = this.client.topValues(queryData);
        } else if (queryType.equals("aggregation")){
            result = this.client.aggregation(queryData);
        } else if (queryType.equals("result")){
            result = this.client.result(queryData);
        } else if (queryType.equals("score")){
            result = this.client.score(queryData);
        }

        return result;
    }

    /**
     * Compare result received from SlicingDice API
     * @param expectedObject the object with expected result
     * @param queryType - the type of the query
     * @param result the result received from SlicingDice API
     */
    private void compareResult(final JSONObject expectedObject, String queryType,
                               final JSONObject result) throws IOException {
        final JSONObject testExpected = expectedObject.getJSONObject("expected");
        final JSONObject expected =
                this.translateFieldNames(expectedObject.getJSONObject("expected"));

        for (final Object key : testExpected.keySet()) {
            final String keyStr = (String)key;
            final Object value = testExpected.get(keyStr);

            if (value.toString().equals("ignore")){
                continue;
            }

            final int expectedSize = expected.getJSONObject(keyStr).length();

            if(expectedSize != result.getJSONObject(keyStr).length() ||
                    !expected.getJSONObject(keyStr).similar(result.getJSONObject(keyStr))) {
                // try second time
                try {
                    TimeUnit.SECONDS.sleep(this.sleepTime * 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final JSONObject secondResult = this.executeQuery(queryType, expectedObject);

                if(expectedSize == secondResult.getJSONObject(keyStr).length() &&
                        expected.getJSONObject(keyStr).
                                similar(secondResult.getJSONObject(keyStr))) {
                    System.out.println("\tPassed at second try!");
                    this.numberOfSuccesses += 1;
                    System.out.println("\tStatus: Passed\n");
                    continue;
                }

                this.numberOfFails += 1;
                this.failedTests.add(expectedObject.getString("name"));

                System.out.println(String.format("\tExpected: \"%1$s\": %2$s", keyStr,
                        expected.getJSONObject(keyStr).toString()));
                System.out.println(String.format("\tResult: \"%1$s\": %2$s", keyStr,
                        result.getJSONObject(keyStr).toString()));
                System.out.println("\tStatus: Failed\n");
                return;
            }

            this.numberOfSuccesses += 1;
            System.out.println("\tStatus: Passed\n");
        }
    }
}
