/*
 * Copyright (C) 2016 Simbiose Ventures.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slicingdice.jslicer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.slicingdice.jslicer.exceptions.api.SDHttpError;
import com.slicingdice.jslicer.exceptions.client.InvalidQueryException;
import com.slicingdice.jslicer.exceptions.client.MaxLimitException;
import com.slicingdice.jslicer.exceptions.client.SlicingDiceKeyException;
import com.slicingdice.jslicer.utils.URLResources;
import com.slicingdice.jslicer.core.Requester;
import com.slicingdice.jslicer.utils.validators.FieldValidator;
import com.slicingdice.jslicer.utils.validators.QueryCountValidator;
import com.slicingdice.jslicer.utils.validators.QueryDataExtractionValidator;
import com.slicingdice.jslicer.utils.validators.TopValuesValidator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.slicingdice.jslicer.core.HandlerResponse;

import okhttp3.Headers;
import okhttp3.Response;


/**
 * The SlicingDice class is a interface to Slicing Dice API.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class SlicingDice {

    private static final String POST = "post";
    private static final String PUT = "put";
    private static final String DELETE = "delete";

    private String apiKey;
    private String masterKey;
    private String customKey;
    private String writeKey;
    private String readKey;
    private int timeout;

    /**
     * A String list with all types of query supported
     */
    private final List<String> queryTypes = Arrays.asList(
            "count/entity", "count/event", "count/entity/total",
            "aggregation", "top_values");
    /**
     * This variable get from enviroment the Slicing Dice API url. If enviroment is
     * empty, his set the url 'https://api.slicingdice.com' how your value.
     */
    private final String baseURL = (System.getenv("SD_API_ADDRESS") != null) ?
            System.getenv("SD_API_ADDRESS") : "https://api.slicingdice.com/v1";

    private int statusCode;
    private Headers headers;
    private boolean usesTestEndPoint;

    public SlicingDice(final String masterKey, final boolean usesTestEndPoint) {
        this.masterKey = masterKey;
        this.timeout = 60;
        this.usesTestEndPoint = usesTestEndPoint;
    }

    public SlicingDice(final String masterKey, final String customKey, final String writeKey,
                       final String readKey, final boolean usesTestEndPoint) {
        this.masterKey = masterKey;
        this.customKey = customKey;
        this.writeKey = writeKey;
        this.readKey = readKey;
        this.timeout = 60;
        this.usesTestEndPoint = usesTestEndPoint;
    }

    public SlicingDice(final String masterKey, final String customKey, final String writeKey,
                       final String readKey, final int timeout, final boolean usesTestEndPoint) {
        this.masterKey = masterKey;
        this.customKey = customKey;
        this.writeKey = writeKey;
        this.readKey = readKey;
        this.timeout = timeout;
        this.usesTestEndPoint = usesTestEndPoint;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    private ArrayList<Object> getCurrentKey() throws SlicingDiceKeyException {
        final String key;
        final int keyLevel;

        if (this.masterKey != null) {
            key = this.masterKey;
            keyLevel = 2;
        } else if (this.customKey != null) {
            key = this.customKey;
            keyLevel = 2;
        } else if (this.writeKey != null) {
            key = this.writeKey;
            keyLevel = 1;
        } else if (this.readKey != null) {
            key = this.readKey;
            keyLevel = 0;
        } else {
            throw new SlicingDiceKeyException("You need put a key.");
        }

        final ArrayList<Object> result = new ArrayList<>();
        result.add(key);
        result.add(keyLevel);

        return result;
    }

    private String getKey(final int levelKey) throws SlicingDiceKeyException {
        final List<Object> currentLevelKey = this.getCurrentKey();
        if ((Integer) currentLevelKey.get(1) == 2) {
            return (String) currentLevelKey.get(0);
        }
        if ((Integer) currentLevelKey.get(1) != levelKey) {
            throw new SlicingDiceKeyException("This key is not allowed to perform this operation.");
        }
        return (String) currentLevelKey.get(0);
    }

    /**
     * Effectively makes the request
     *
     * @param url  A url String to make request
     * @param data An Object to send in request
     * @return A JSONObject with json request result
     * @throws IOException
     */
    private JSONObject makeRequest(final String url, final Object data, final String reqType,
                                   final int keyLevel) throws IOException {
        final String apiKey = this.getKey(keyLevel);
        Response resp = null;

        if (reqType.equals(POST)) {
            resp = Requester.post(url, data.toString(), apiKey, timeout);
        } else if (reqType.equals(PUT)) {
            resp = Requester.put(url, data.toString(), apiKey, timeout);
        } else if (reqType.equals(DELETE)) {
            resp = Requester.delete(url, apiKey, timeout);
        }

        return handlerResponse(resp);
    }

    private JSONObject makeRequest(final String url, final int keyLevel) throws IOException {
        final String apiKey = this.getKey(keyLevel);
        final Response resp = Requester.get(url, apiKey, timeout);

        return handlerResponse(resp);
    }

    private JSONObject handlerResponse(final Response resp) throws IOException {
        try {
            final HandlerResponse responseData = new HandlerResponse(resp.body().string(),
                    resp.headers(), resp.code());

            if (responseData.requestSuccessful()) {
                if (this.checkRequest(resp)) {
                    populateResult(responseData);
                    return new JSONObject(responseData.getResult());
                }
            }

            return null;
        } finally {
            resp.close();
        }
    }

    /**
     * Check if request dont't have client or server errors
     *
     * @param response A okhttp3.Response object of Slicing Dice request
     * @return true if the request don't have errors in client or server
     * @throws SDHttpError
     */
    private boolean checkRequest(final Response response) throws SDHttpError {
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() <= 499) {
                throw new SDHttpError("Client Error " + response.code() + "(" + response.message()
                        + ")");
            }
            if (response.code() >= 500 && response.code() <= 600) {
                throw new SDHttpError("Server Error " + response.code() + "(" + response.message()
                        + ")");
            }
        }
        return true;
    }

    /**
     * Populates properties after request make
     *
     * @param response A okhttp3.Response object of Slicing Dice request
     * @throws IOException
     */
    private void populateResult(final HandlerResponse response) throws IOException {
        this.statusCode = response.getStatusCode();
        this.headers = response.getHeaders();
    }

    private String wrapperTest() throws IOException {
        if (this.usesTestEndPoint) {
            return this.baseURL + "/test";
        }
        return this.baseURL;
    }

    /**
     * Create field in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice field format
     * @return A JSONObject with json request result if your field is valid
     * @throws IOException
     */
    private JSONObject wrapperCreateField(final JSONObject data, final String url)
            throws IOException {
        final FieldValidator fieldValidator = new FieldValidator(data);

        if (fieldValidator.validator()) {
            return this.makeRequest(url, data, POST, 2);
        }

        return null;
    }

    /**
     * Create field in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice field format
     * @return A JSONObject with json request result if your field is valid
     * @throws IOException
     */
    public JSONObject createField(final JSONObject data) throws IOException {
        final String url = this.wrapperTest() + URLResources.FIELD.url;
        return this.wrapperCreateField(data, url);
    }

    /**
     * Create field in Slicing Dice
     *
     * @param dataArray A JSONArray with many JSONObjects in the Slicing Dice field format
     * @return A JSONObject with json request result if your field is valid
     * @throws IOException
     */
    public JSONObject createField(final JSONArray dataArray) throws IOException {
        final String url = this.wrapperTest() + URLResources.FIELD.url;
        final JSONObject result = new JSONObject();
        for (int i = 0; i < dataArray.length(); i++) {
            final JSONObject data = dataArray.getJSONObject(i);
            final JSONObject partialResult = this.wrapperCreateField(data, url);
            result.put(String.valueOf(i), partialResult);
        }

        return result;
    }

    /**
     * Get all fields.
     *
     * @return All fields(active and inactive).
     * @throws IOException
     */
    public JSONObject getFields() throws IOException {
        final String url = this.wrapperTest() + URLResources.FIELD.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Index data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /index.
     *
     * @param data A JSON object in the SlicingDice index format
     * @return A JSONObject with json request result if your indexation is valid
     * @throws IOException
     */
    public JSONObject index(final JSONObject data) throws IOException {
        final String url = this.wrapperTest() + URLResources.INDEX.url;
        return this.makeRequest(url, data, POST, 1);
    }

    /**
     * Index data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /index.
     *
     * @param data             A JSON object in the SlicingDice index format
     * @param autoCreateFields if true the indexation will automatically create non-existent fields
     * @return A JSONObject with json request result if your indexation is valid
     * @throws IOException
     */
    public JSONObject index(final JSONObject data, final boolean autoCreateFields)
            throws IOException {
        data.put("auto-create-fields", autoCreateFields);
        final String url = this.wrapperTest() + URLResources.INDEX.url;
        return this.makeRequest(url, data, POST, 1);
    }

    /**
     * Make a count query in Slicing Dice
     *
     * @param url   A url to make request
     * @param query An Object count query
     * @return A JSONObject with count query result
     * @throws IOException
     */
    private JSONObject countQueryWrapper(final String url, final Object query)
            throws IOException {
        final QueryCountValidator queryValidator = new QueryCountValidator(query);
        if (!queryValidator.validator()) {
            return null;
        }
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a data extraction query in Slicing Dice
     *
     * @param url   A url to make request
     * @param query A JSONObject data extraction query
     * @return A JSONObject with data extraction query result
     * @throws IOException
     */
    private JSONObject dataExtractionWrapper(final String url, final JSONObject query)
            throws IOException {
        final QueryDataExtractionValidator queryValidator = new QueryDataExtractionValidator(query);
        if (!queryValidator.validator()) {
            return null;
        }
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Get all getProjects in your account.
     *
     * @return All getProjects(active and inactive).
     * @throws IOException
     */
    public JSONObject getProjects() throws IOException {
        final String url = this.wrapperTest() + URLResources.PROJECT.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Make a count entity query in Slicing Dice API
     *
     * @param query An Object count entity query
     * @return A JSONObject with count entity query result
     * @throws IOException
     */
    public JSONObject countEntity(final Object query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a total query in Slicing Dice API
     *
     * @return A JSONObject with total query result
     * @throws IOException
     */
    public JSONObject countEntityTotal() throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        return this.makeRequest(url, 0);
    }

    /**
     * Make a count event query in Slicing Dice API
     *
     * @param query An Object count event query
     * @return A JSONObject with count event query result
     * @throws IOException
     */
    public JSONObject countEvent(final Object query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_COUNT_EVENT.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a aggregation query in Slicing Dice API
     *
     * @param query A JSONObject aggregation query
     * @return A JSONObject with aggregation query result
     * @throws IOException
     */
    private JSONObject wrapperAggregation(final JSONObject query, final String url)
            throws IOException {
        if (!query.has("query")) {
            throw new InvalidQueryException("The aggregation query must have up the key 'query'.");
        }
        if (query.length() > 5) {
            throw new MaxLimitException("The aggregation query must have up to 5 fields per request.");
        }
        return makeRequest(url, query, POST, 0);
    }

    /**
     * Make a aggregation query in Slicing Dice API
     *
     * @param query A JSONObject aggregation query
     * @return A JSONObject with aggregation query result
     * @throws IOException
     */
    public JSONObject aggregation(final JSONObject query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_AGGREGATION.url;
        return this.wrapperAggregation(query, url);
    }

    /**
     * Make a top values query in Slicing Dice API
     *
     * @param query A JSONObject top values query
     * @return A JSONObject with top values query result
     * @throws IOException
     */
    private JSONObject wrapperTopValues(final JSONObject query, final String url)
            throws IOException {
        final TopValuesValidator topValuesValidator = new TopValuesValidator(query);
        if (topValuesValidator.validator()) {
            return makeRequest(url, query, POST, 0);
        }
        return null;
    }

    /**
     * Make a top values query in Slicing Dice API
     *
     * @param query A JSONObject top values query
     * @return A JSONObject with top values query result
     * @throws IOException
     */
    public JSONObject topValues(final JSONObject query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_TOP_VALUES.url;
        return this.wrapperTopValues(query, url);
    }

    /**
     * Make a exists entity query in Slicing Dice API
     *
     * @param ids A JSONArray exists entity query
     * @return A JSONObject with exists entity query result
     * @throws IOException
     */
    private JSONObject wrapperExistsEntity(final JSONArray ids, final String url)
            throws IOException, MaxLimitException {
        if (ids.length() > 100) {
            throw new MaxLimitException("The query exists entity must have up to 100 ids.");
        }
        final JSONObject query = new JSONObject().put("ids", ids);
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a exists entity query in Slicing Dice API
     *
     * @param ids A JSONArray exists entity query
     * @return A JSONObject with exists entity query result
     * @throws IOException
     */
    public JSONObject existsEntity(final JSONArray ids) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_EXISTS_ENTITY.url;
        return this.wrapperExistsEntity(ids, url);
    }

    /**
     * Query SlicingDice API for saved queries
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @return A JSONObject with get saved query result
     * @throws IOException
     */
    public JSONObject getSavedQuery(final String queryName) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, 0);
    }

    /**
     * Query SlicingDice API for all saved queries
     *
     * @return A JSONObject with get saved query result
     * @throws IOException
     */
    public JSONObject getSavedQueries() throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Delete a previous saved query on SlicingDice API
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @return A JSONObject with get saved query result
     * @throws IOException
     */
    public JSONObject deleteSavedQuery(final String queryName) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, null, DELETE, 2);
    }

    /**
     * Create a saved query in Slicing Dice API
     *
     * @param query A JSONObject saved query
     * @return A JSONObject with saved query if request was successful
     * @throws IOException
     */
    private JSONObject wrapperCreateSavedQuery(final JSONObject query, final String url)
            throws IOException {
        if (query.has("name") && query.has("type") && query.has("query")) {
            final String queryType = query.getString("type");
            if (!this.queryTypes.contains(queryType)) {
                throw new InvalidQueryException("The query saved has a invalid type.(" + queryType
                        + ").");
            }
            return this.makeRequest(url, query, POST, 1);
        }
        return null;
    }

    /**
     * Create a saved query in Slicing Dice API
     *
     * @param query A JSONObject saved query
     * @return A JSONObject with saved query if request was successful
     * @throws IOException
     */
    public JSONObject createSavedQuery(final JSONObject query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_SAVED.url;
        return this.wrapperCreateSavedQuery(query, url);
    }

    /**
     * Update a saved query in Slicing Dice API
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param query     A JSONObject saved query
     * @return A JSONObject with new saved query if request was successful
     * @throws IOException
     */
    public JSONObject updateSavedQuery(final String queryName, final JSONObject query)
            throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, query, PUT, 2);
    }

    /**
     * Make a data extraction score query in Slicing Dice API
     *
     * @param query A JSONObject data extraction score query
     * @return A JSONObject with data extraction score query result
     * @throws IOException
     */
    public JSONObject score(final JSONObject query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_DATA_EXTRACTION_SCORE.url;
        return dataExtractionWrapper(url, query);
    }

    /**
     * Make a data extraction result query in Slicing Dice API
     *
     * @param query A JSONObject data extraction result query
     * @return A JSONObject with result of data extraction result query
     * @throws IOException
     */
    public JSONObject result(final JSONObject query) throws IOException {
        final String url = this.wrapperTest() + URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        return dataExtractionWrapper(url, query);
    }

}
