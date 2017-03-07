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

    private String _apiKey;
    private String _masterKey;
    private String _customKey;
    private String _writeKey;
    private String _readKey;
    private int _timeout;

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

    public SlicingDice(final String masterKey) {
        this._masterKey = masterKey;
        _timeout = 60;
    }

    public SlicingDice(final String masterKey, final String customKey, final String writeKey,
                       final String readKey) {
        this._masterKey = masterKey;
        this._customKey = customKey;
        this._writeKey = writeKey;
        this._readKey = readKey;
        _timeout = 60;
    }

    public SlicingDice(final String masterKey, final String customKey, final String writeKey,
                       final String readKey, final int timeout) {
        this._masterKey = masterKey;
        this._customKey = customKey;
        this._writeKey = writeKey;
        this._readKey = readKey;
        _timeout = timeout;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    private ArrayList<Object> getCurrentKey() throws SlicingDiceKeyException {
        if (this._masterKey != null){
            final ArrayList<Object> result = new ArrayList<>();
            result.add(this._masterKey);
            result.add(2);
            return result;
        } else if (this._customKey != null){
            final ArrayList<Object> result = new ArrayList<>();
            result.add(this._customKey);
            result.add(2);
            return result;
        } else if (this._writeKey != null){
            final ArrayList<Object> result = new ArrayList<>();
            result.add(this._writeKey);
            result.add(1);
            return result;
        } else if (this._readKey != null){
            final ArrayList<Object> result = new ArrayList<>();
            result.add(this._readKey);
            result.add(0);
            return result;
        }

        throw new SlicingDiceKeyException("You need put a key.");
    }

    private String getKey(final int levelKey) throws SlicingDiceKeyException {
        final List<Object> currentLevelKey = this.getCurrentKey();
        if ((Integer)currentLevelKey.get(1) == 2){
            return (String)currentLevelKey.get(0);
        }
        if ((Integer)currentLevelKey.get(1) != levelKey){
            throw new SlicingDiceKeyException("This key is not allowed to perform this operation.");
        }
        return (String)currentLevelKey.get(0);
    }

    /**
     * Effectively makes the request
     *
     * @param url  A url String to make request
     * @param data A JSONObject to send in request
     * @return A String with json request result
     * @throws IOException
     */
    private JSONObject makeRequest(final String url, final JSONObject data, final String reqType,
                                   final int keyLevel) throws IOException {
        final String apiKey = this.getKey(keyLevel);
        Response resp = null;

        if (reqType.equals(POST)) {
            resp = Requester.post(url, data.toString(), apiKey, _timeout);
        } else if (reqType.equals(PUT)) {
            resp = Requester.put(url, data.toString(), apiKey, _timeout);
        } else if (reqType.equals(DELETE)) {
            resp = Requester.delete(url, apiKey, _timeout);
        }

        return handlerResponse(resp);
    }

    private JSONObject makeRequest(final String url, final int keyLevel) throws IOException {
        final String apiKey = this.getKey(keyLevel);
        final Response resp = Requester.get(url, apiKey, _timeout);

        return handlerResponse(resp);
    }

    private JSONObject handlerResponse(final Response resp) throws IOException {
        final HandlerResponse responseData = new HandlerResponse(resp.body().string(),
                resp.headers(), resp.code());

        if (responseData.requestSuccessful()) {
            if (this.checkRequest(resp)) {
                populateResult(responseData);
                return new JSONObject(responseData.getResult());
            }
        }

        return null;
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

    private String wrapperTest(final boolean test) throws IOException {
        if (test) {
            return this.baseURL + "/test";
        }
        return this.baseURL;
    }

    /**
     * Create field in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice field format
     * @return A String with json request result if your field is valid
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
     * @return A String with json request result if your field is valid
     * @throws IOException
     */
    public JSONObject createField(final JSONObject data) throws IOException {
        final String url = baseURL + URLResources.FIELD.url;
        return this.wrapperCreateField(data, url);
    }

    /**
     * Create field in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice field format
     * @param test if true the field will be created on test end-point otherwise on production
     *             end-point
     * @return A String with json request result if your field is valid
     * @throws IOException
     */
    public JSONObject createField(final JSONObject data, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.FIELD.url;
        return this.wrapperCreateField(data, url);
    }

    /**
     * Get all fields.
     *
     * @return All fields(active and inactive).
     * @throws IOException
     */
    public JSONObject getFields() throws IOException {
        final String url = this.baseURL + URLResources.FIELD.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Get all fields.
     *
     * @param test if true the request will be to test end-point
     * @return All fields(active and inactive).
     * @throws IOException
     */
    public JSONObject getFields(final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.FIELD.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Index data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /index.
     *
     * @param data A JSON object in the SlicingDice index format
     * @return A String with json request result if your indexation is valid
     * @throws IOException
     */
    public JSONObject index(final JSONObject data) throws IOException {
        final String url = this.baseURL + URLResources.INDEX.url;
        return this.makeRequest(url, data, POST, 1);
    }

    /**
     * Index data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /index.
     *
     * @param data A JSON object in the SlicingDice index format
     * @param test if true the request will be to test end-point
     * @return A String with json request result if your indexation is valid
     * @throws IOException
     */
    public JSONObject index(final JSONObject data, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.INDEX.url;
        return this.makeRequest(url, data, POST, 1);
    }

    /**
     * Index data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /index.
     *
     * @param data A JSON object in the SlicingDice index format
     * @param autoCreateFields if true the indexation will automatically create non-existent fields
     * @param test if true the request will be to test end-point
     * @return A String with json request result if your indexation is valid
     * @throws IOException
     */
    public JSONObject index(final JSONObject data, final boolean autoCreateFields,
                            final boolean test) throws IOException {
        data.put("auto-create-fields", autoCreateFields);
        String url = this.wrapperTest(test);
        url += URLResources.INDEX.url;
        return this.makeRequest(url, data, POST, 1);
    }

    /**
     * Make a count query in Slicing Dice
     *
     * @param url   A url to make request
     * @param query A JSONObject count query
     * @return A String with count query result
     * @throws IOException
     */
    private JSONObject countQueryWrapper(final String url, final JSONObject query)
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
     * @return A String with data extraction query result
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
        final String url = this.baseURL + URLResources.PROJECT.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Get all getProjects in your account.
     *
     * @param test if true the request will be to test end-point
     * @return All getProjects(active and inactive).
     * @throws IOException
     */
    public JSONObject getProjects(final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.PROJECT.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Make a count entity query in Slicing Dice API
     *
     * @param query A JSONObject count entity query
     * @return A String with count entity query result
     * @throws IOException
     */
    public JSONObject countEntity(final JSONObject query) throws IOException {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a count entity query in Slicing Dice API
     *
     * @param query A JSONObject count entity query
     * @param test if true the request will be to test end-point
     * @return A String with count entity query result
     * @throws IOException
     */
    public JSONObject countEntity(final JSONObject query, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a total query in Slicing Dice API
     *
     * @return A String with total query result
     * @throws IOException
     */
    public JSONObject countEntityTotal() throws IOException {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        return this.makeRequest(url, 0);
    }

    /**
     * Make a total query in Slicing Dice API
     *
     * @param test if true the request will be to test end-point
     * @return A String with total query result
     * @throws IOException
     */
    public JSONObject countEntityTotal(final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        return this.makeRequest(url, 0);
    }

    /**
     * Make a count event query in Slicing Dice API
     *
     * @param query A JSONObject count event query
     * @return A String with count event query result
     * @throws IOException
     */
    public JSONObject countEvent(final JSONObject query) throws IOException {
        final String url = this.baseURL + URLResources.QUERY_COUNT_EVENT.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a count event query in Slicing Dice API
     *
     * @param query A JSONObject count event query
     * @param test if true the request will be to test end-point
     * @return A String with count event query result
     * @throws IOException
     */
    public JSONObject countEvent(final JSONObject query, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_COUNT_EVENT.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a aggregation query in Slicing Dice API
     *
     * @param query A JSONObject aggregation query
     * @return A String with aggregation query result
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
     * @return A String with aggregation query result
     * @throws IOException
     */
    public JSONObject aggregation(final JSONObject query) throws IOException {
        final String url = this.baseURL + URLResources.QUERY_AGGREGATION.url;
        return this.wrapperAggregation(query, url);
    }

    /**
     * Make a aggregation query in Slicing Dice API
     *
     * @param query A JSONObject aggregation query
     * @param test if true the request will be to test end-point
     * @return A String with aggregation query result
     * @throws IOException
     */
    public JSONObject aggregation(final JSONObject query, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_AGGREGATION.url;
        return this.wrapperAggregation(query, url);
    }

    /**
     * Make a top values query in Slicing Dice API
     *
     * @param query A JSONObject top values query
     * @return A String with top values query result
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
     * @return A String with top values query result
     * @throws IOException
     */
    public JSONObject topValues(final JSONObject query) throws IOException {
        final String url = baseURL + URLResources.QUERY_TOP_VALUES.url;
        return this.wrapperTopValues(query, url);
    }

    /**
     * Make a top values query in Slicing Dice API
     *
     * @param query A JSONObject top values query
     * @param test if true the request will be to test end-point
     * @return A String with top values query result
     * @throws IOException
     */
    public JSONObject topValues(final JSONObject query, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_TOP_VALUES.url;
        return this.wrapperTopValues(query, url);
    }

    /**
     * Make a exists entity query in Slicing Dice API
     *
     * @param ids A JSONArray exists entity query
     * @return A String with exists entity query result
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
     * @return A String with exists entity query result
     * @throws IOException
     */
    public JSONObject existsEntity(final JSONArray ids) throws IOException {
        final String url = baseURL + URLResources.QUERY_EXISTS_ENTITY.url;
        return this.wrapperExistsEntity(ids, url);
    }

    /**
     * Make a exists entity query in Slicing Dice API
     *
     * @param ids A JSONArray exists entity query
     * @param test if true the request will be to test end-point
     * @return A String with exists entity query result
     * @throws IOException
     */
    public JSONObject existsEntity(final JSONArray ids, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_EXISTS_ENTITY.url;
        return this.wrapperExistsEntity(ids, url);
    }

    /**
     * Query SlicingDice API for saved queries
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @return A String with get saved query result
     * @throws IOException
     */
    public JSONObject getSavedQuery(final String queryName) throws IOException {
        final String url = baseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, 0);
    }

    /**
     * Query SlicingDice API for saved queries
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param test if true the request will be to test end-point
     * @return A String with get saved query result
     * @throws IOException
     */
    public JSONObject getSavedQuery(final String queryName, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, 0);
    }

    /**
     * Query SlicingDice API for all saved queries
     *
     * @return A String with get saved query result
     * @throws IOException
     */
    public JSONObject getSavedQueries() throws IOException {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Query SlicingDice API for all saved queries
     *
     * @param test if true the request will be to test end-point
     * @return A String with get saved query result
     * @throws IOException
     */
    public JSONObject getSavedQueries(final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Delete a previous saved query on SlicingDice API
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @return A String with get saved query result
     * @throws IOException
     */
    public JSONObject deleteSavedQuery(final String queryName) throws IOException {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, null, DELETE, 2);
    }

    /**
     * Delete a previous saved query on SlicingDice API
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param test if true the request will be to test end-point
     * @return A String with get saved query result
     * @throws IOException
     */
    public JSONObject deleteSavedQuery(final String queryName, final boolean test)
            throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, null, DELETE, 2);
    }

    /**
     * Create a saved query in Slicing Dice API
     *
     * @param query A JSONObject saved query
     * @return A String with saved query if request was successful
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
     * @return A String with saved query if request was successful
     * @throws IOException
     */
    public JSONObject createSavedQuery(final JSONObject query) throws IOException {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url;
        return this.wrapperCreateSavedQuery(query, url);
    }

    /**
     * Create a saved query in Slicing Dice API
     *
     * @param query A JSONObject saved query
     * @param test if true the request will be to test end-point
     * @return A String with saved query if request was successful
     * @throws IOException
     */
    public JSONObject createSavedQuery(final JSONObject query, final boolean test)
            throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url;
        return this.wrapperCreateSavedQuery(query, url);
    }

    /**
     * Update a saved query in Slicing Dice API
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param query A JSONObject saved query
     * @return A String with new saved query if request was successful
     * @throws IOException
     */
    public JSONObject updateSavedQuery(final String queryName, final JSONObject query)
            throws IOException {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, query, PUT, 2);
    }

    /**
     * Update a saved query in Slicing Dice API
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param query A JSONObject saved query
     * @param test if true the request will be to test end-point
     * @return A String with new saved query if request was successful
     * @throws IOException
     */
    public JSONObject updateSavedQuery(final String queryName, final JSONObject query,
                                       final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, query, PUT, 2);
    }

    /**
     * Make a data extraction score query in Slicing Dice API
     *
     * @param query A JSONObject data extraction score query
     * @return A String with data extraction score query result
     * @throws IOException
     */
    public JSONObject score(final JSONObject query) throws IOException {
        final String url = this.baseURL + URLResources.QUERY_DATA_EXTRACTION_SCORE.url;
        return dataExtractionWrapper(url, query);
    }

    /**
     * Make a data extraction score query in Slicing Dice API
     *
     * @param query A JSONObject data extraction score query
     * @param test if true the request will be to test end-point
     * @return A String with data extraction score query result
     * @throws IOException
     */
    public JSONObject score(final JSONObject query, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_DATA_EXTRACTION_SCORE.url;
        return dataExtractionWrapper(url, query);
    }

    /**
     * Make a data extraction result query in Slicing Dice API
     *
     * @param query A JSONObject data extraction result query
     * @return A String with result of data extraction result query
     * @throws IOException
     */
    public JSONObject result(final JSONObject query) throws IOException {
        final String url = baseURL + URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        return dataExtractionWrapper(url, query);
    }

    /**
     * Make a data extraction result query in Slicing Dice API
     *
     * @param query A JSONObject data extraction result query
     * @param test if true the request will be to test end-point
     * @return A String with result of data extraction result query
     * @throws IOException
     */
    public JSONObject result(final JSONObject query, final boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        return dataExtractionWrapper(url, query);
    }
}
