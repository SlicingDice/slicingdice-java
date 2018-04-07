package com.slicingdice.jslicer;

import com.slicingdice.jslicer.core.Requester;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

public class SlicingDiceTester {

    // The SlicingDice client
    private final SlicingDice client;

    private boolean verbose = false;

    // Translation table for columns with timestamp
    private JSONObject columnTranslation;

    // Sleep time in seconds
    private long sleepTime;

    // Directory containing examples to test
    private String path;

    // Examples file format
    private String fileExtension;

    public int numberOfSuccesses;

    public int numberOfFails;

    public ArrayList<Object> failedTests;

    private boolean perTestInsertion;

    private boolean insertSqlData;

    public SlicingDiceTester(final String apiKey) {
        this.client = new SlicingDice(apiKey);
        this.loadConfigTest();
    }

    public SlicingDiceTester(final String apiKey, final boolean verbose) {
        this.client = new SlicingDice(apiKey);
        this.verbose = verbose;
        this.loadConfigTest();
    }

    private void loadConfigTest() {
        this.sleepTime = 5;
        this.path = "src/test/java/com/slicingdice/jslicer/examples/";
        this.fileExtension = ".json";
        this.numberOfSuccesses = 0;
        this.numberOfFails = 0;
        this.failedTests = new ArrayList<>();
    }

    /**
     * Run tests
     *
     * @param queryType the query type
     */
    public void runTests(final String queryType) throws ExecutionException, InterruptedException {
        final JSONArray testData = this.loadTestData(queryType);
        final int numberOfTests = testData.length();

        this.perTestInsertion = testData.getJSONObject(0).has("insert");
        if (!this.perTestInsertion && this.insertSqlData) {
            final JSONArray insertionData = this.loadInsertionData(queryType);
            for (final Object insertCommand : insertionData) {
                this.client.insert((JSONObject) insertCommand).get();
            }
            Thread.sleep(this.sleepTime);
        }

        for (int i = 0; i < numberOfTests; i++) {
            final JSONObject testObject = (JSONObject) testData.get(i);
            this.emptyColumnTranslation();

            System.out.println(String.format("(%1$d/%2$d) Executing test \"%3$s\"", i + 1,
                    numberOfTests, testObject.getString("name")));

            if (testObject.has("description")) {
                System.out.println(String.format("\tDescription: %s",
                        testObject.get("description")));
            }

            System.out.println(String.format("\tQuery type: %s", queryType));
            JSONObject result = null;
            try {
                if (this.perTestInsertion) {
                    this.createColumns(testObject);
                    this.insertData(testObject);
                }
                result = this.executeQuery(queryType, testObject);
            } catch (final Exception e) {
                result = new JSONObject()
                        .put("result", new JSONObject()
                                .put("error", e.toString()));
            }

            try {
                this.compareResult(testObject, queryType, result);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void emptyColumnTranslation() {
        this.columnTranslation = new JSONObject();
    }

    /**
     * Load test data from examples files
     *
     * @param queryType the query type
     * @return JSONArray with test data
     */
    private JSONArray loadTestData(final String queryType) {
        return loadData(queryType, "");
    }

    /**
     * Load insertion data from examples files
     *
     * @param queryType the query type
     * @return JSONArray with insertion data
     */
    private JSONArray loadInsertionData(final String queryType) {
        return loadData(queryType, "_insert");
    }

    private JSONArray loadData(final String queryType, final String file_suffix) {
        final String file = new File(this.path + queryType + file_suffix
                + this.fileExtension).getAbsolutePath();
        String content = null;
        try {
            content = new Scanner(new File(file)).useDelimiter("\\Z").next();
            return new JSONArray(content);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create columns on SlicingDice API
     *
     * @param columnObject the column object to create
     */
    private void createColumns(final JSONObject columnObject) {
        final JSONArray columns = columnObject.getJSONArray("columns");
        final boolean isSingular = columns.length() == 1;
        String columnOrColumns = null;

        if (isSingular) {
            columnOrColumns = "column";
        } else {
            columnOrColumns = "columns";
        }

        System.out.println(String.format("\tCreating %1$d %2$s", columns.length(), columnOrColumns));

        for (final Object column : columns) {
            final JSONObject columnDict = (JSONObject) column;
            this.addTimestampToColumnName(columnDict);
            // call client command to create columns
            try {
                this.client.createColumn(columnDict).get();
            } catch (final InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (this.verbose) {
                System.out.println(String.format("\t\t- %s", columnDict.getString("api-name")));
            }
        }
    }

    /**
     * Add timestamp to column name
     *
     * @param column the column to put timestamp
     */
    private void addTimestampToColumnName(final JSONObject column) {
        final String oldName = "\"" + column.getString("api-name");

        final String timestamp = this.getTimestamp();
        column.put("name", column.get("name") + timestamp);
        column.put("api-name", column.get("api-name") + timestamp);

        final String newName = "\"" + column.getString("api-name");
        this.columnTranslation.put(oldName, newName);
    }

    /**
     * Get actual timestamp
     *
     * @return timestamp converted to string
     */
    private String getTimestamp() {
        final Long currentTime = System.currentTimeMillis() * 10;
        return currentTime.toString();
    }

    /**
     * Insert data to SlicingDice API
     *
     * @param insertionObject the data to insert on SlicingDice
     */
    private void insertData(final JSONObject insertionObject) {
        final JSONObject insert = insertionObject.getJSONObject("insert");
        final boolean isSingular = insert.length() == 1;
        final String entityOrEntities;
        if (isSingular) {
            entityOrEntities = "entity";
        } else {
            entityOrEntities = "entities";
        }
        System.out.println(String.format("\tInserting %1$d %2$s", insert.length(), entityOrEntities));

        final JSONObject insertData = this.translateColumnNames(insert);

        // call client command to insert data
        try {
            this.client.insert(insertData).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            // Wait a few seconds so the data can be inserted by SlicingDice
            TimeUnit.SECONDS.sleep(this.sleepTime);
        } catch (final InterruptedException e) {
            e.printStackTrace();
            System.out.println("An error occurred while processing your query on SlicingDice");
        }
    }

    /**
     * Translate column name to use timestamp
     *
     * @param column the column to translate
     * @return the new column translated
     */
    private JSONObject translateColumnNames(final JSONObject column) {
        String dataString = column.toString();

        for (final Object oldName : this.columnTranslation.keySet()) {
            final String oldNameStr = (String) oldName;
            final String newName = this.columnTranslation.getString(oldNameStr);

            dataString = dataString.replaceAll(oldNameStr, newName);
        }
        return new JSONObject(dataString);
    }

    /**
     * Execute query
     *
     * @param queryType the type of the query
     * @param query     the query to send to SlicingDice API
     * @return the result of the query
     */
    private JSONObject executeQuery(final String queryType, final JSONObject query)
            throws ExecutionException, InterruptedException {
        final JSONObject queryData;
        if (this.perTestInsertion) {
            queryData = this.translateColumnNames(query.getJSONObject("query"));
        } else {
            queryData = query;
        }

        System.out.println("\tQuerying");

        if (this.verbose) {
            System.out.println(String.format("\t\t- %s", queryData));
        }

        JSONObject result = null;
        // call client command to make a query
        switch (queryType) {
            case "count_entity":
                result = Requester.responseToJson(this.client.countEntity(queryData).get());
                break;
            case "count_event":
                result = Requester.responseToJson(this.client.countEvent(queryData).get());
                break;
            case "top_values":
                result = Requester.responseToJson(this.client.topValues(queryData).get());
                break;
            case "aggregation":
                result = Requester.responseToJson(this.client.aggregation(queryData).get());
                break;
            case "result":
                result = Requester.responseToJson(this.client.result(queryData).get());
                break;
            case "score":
                result = Requester.responseToJson(this.client.score(queryData).get());
                break;
            case "sql":
                result = Requester.responseToJson(this.client.sql(queryData.getString("query")).get());
                break;
        }

        return result;
    }

    /**
     * Compare result received from SlicingDice API
     *
     * @param expectedObject the object with expected result
     * @param queryType      - the type of the query
     * @param result         the result received from SlicingDice API
     */
    private void compareResult(final JSONObject expectedObject, final String queryType,
                               final JSONObject result) throws IOException {
        final JSONObject testExpected = expectedObject.getJSONObject("expected");
        final JSONObject expected;
        if (this.perTestInsertion) {
            expected = this.translateColumnNames(expectedObject.getJSONObject("expected"));
        } else {
            expected = expectedObject.getJSONObject("expected");
        }

        for (final Object key : testExpected.keySet()) {
            final String keyStr = (String) key;
            final Object value = testExpected.get(keyStr);

            if (value.toString().equals("ignore")) {
                continue;
            }

            boolean testFailed = false;

            if (!result.has(keyStr)) {
                // try second time
                if (testSecondTime(expectedObject, queryType, expected, keyStr)) {
                    continue;
                }

                testFailed = true;
            } else {
                if (!this.compareJsonValue(expected.get(keyStr),
                        result.get(keyStr))) {
                    // try second time
                    if (testSecondTime(expectedObject, queryType, expected, keyStr)) {
                        continue;
                    }

                    testFailed = true;
                }
            }

            if (testFailed) {
                this.numberOfFails += 1;
                this.failedTests.add(expectedObject.getString("name"));

                System.out.println(String.format("\tExpected: \"%1$s\": %2$s", keyStr,
                        expected.getJSONObject(keyStr).toString()));
                System.out.println(String.format("\tResult: \"%1$s\": %2$s", keyStr,
                        result.getJSONObject(keyStr).toString()));
                System.out.println("\tStatus: Failed\n");
                return;
            } else {
                this.numberOfSuccesses += 1;
                System.out.println("\tStatus: Passed\n");
            }
        }
    }

    /**
     * If first query doesn't return as expected we will try another time
     *
     * @param expectedObject -
     * @param queryType      - type of the query to send to SlicingDice
     * @param expected       = the expected json
     * @param key            - the json key to test
     * @return true if second test succeed and false otherwise
     * @throws IOException
     */
    private boolean testSecondTime(final JSONObject expectedObject, final String queryType,
                                   final JSONObject expected, final String key) {
        try {
            TimeUnit.SECONDS.sleep(this.sleepTime * 3);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        final JSONObject secondResult;
        try {
            secondResult = this.executeQuery(queryType, expectedObject);

            if (this.compareJsonValue(expected.get(key), secondResult.get(key))) {
                System.out.println("\tPassed at second try!");
                this.numberOfSuccesses += 1;
                System.out.println("\tStatus: Passed\n");
                return true;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Compare two JSONObjects
     *
     * @param expected - The json with the expected result
     * @param got      = The json returned by the SlicingDice API
     * @return - true if the two json's are equal and false otherwise
     */
    private boolean compareJson(final JSONObject expected, final JSONObject got) {
        if (expected.length() != got.length()) {
            return false;
        }

        final Set keySet = expected.keySet();
        final Iterator iterator = keySet.iterator();

        while (iterator.hasNext()) {
            final String name = (String) iterator.next();
            final Object valueExpected = expected.get(name);
            final Object valueGot = got.get(name);
            if (!this.compareJsonValue(valueExpected, valueGot)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compare two JSONArrays
     *
     * @param expected - The json with the expected result
     * @param got      = The json returned by the SlicingDice API
     * @return - true if the two json's are equal and false otherwise
     */
    private boolean compareJsonArray(final JSONArray expected, final JSONArray got) {
        if (expected.length() != got.length()) {
            return false;
        }

        for (int i = 0; i < expected.length(); ++i) {
            final Object valueExpected = expected.get(i);
            boolean hasTrue = false;
            for (int j = 0; j < got.length(); j++) {
                final Object valueGot = got.get(j);
                if (this.compareJsonValue(valueExpected, valueGot)) {
                    hasTrue = true;
                }
            }

            if (!hasTrue) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compare two json values
     *
     * @param valueExpected - the expected value
     * @param valueGot      - the received value
     * @return true if the two values are equal and false otherwise
     */
    private boolean compareJsonValue(final Object valueExpected, final Object valueGot) {
        try {
            if (valueExpected instanceof JSONObject) {
                if (!this.compareJson((JSONObject) valueExpected, (JSONObject) valueGot)) {
                    return false;
                }
            } else if (valueExpected instanceof JSONArray) {
                if (!this.compareJsonArray((JSONArray) valueExpected, (JSONArray) valueGot)) {
                    return false;
                }
            } else if (!valueExpected.equals(valueGot)) {
                if (valueExpected instanceof Integer && valueGot instanceof Double ||
                        valueExpected instanceof Double && valueGot instanceof Integer) {
                    final Number expectedInteger = (Number) valueExpected;
                    final Number gotInteger = (Number) valueGot;
                    if (expectedInteger.intValue() != gotInteger.intValue()) {
                        return false;
                    }
                } else {
                    return Objects.equals(valueExpected, valueGot);
                }
                return true;
            }
        } catch (final ClassCastException e) {
            return false;
        }

        return true;
    }
}
