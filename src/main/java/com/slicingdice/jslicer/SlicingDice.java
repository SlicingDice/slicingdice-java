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

    private String _apiKey;
    private String _masterKey;
    private String _customKey;
    private String _writeKey;
    private String _readKey;
    private int _timeout;

    /**
     * A String list with all types of query supported
     */
    private List<String> queryTypes = Arrays.asList(
            "count/entity", "count/event", "count/entity/total",
            "aggregation", "top_values");
    /**
     * This variable get from enviroment the Slicing Dice API url. If enviroment is
     * empty, his set the url 'https://api.slicingdice.com' how your value.
     */
    private String BaseURL = (System.getenv("SD_API_ADDRESS") != null) ? System.getenv("SD_API_ADDRESS")
            : "https://api.slicingdice.com/v1";

    private int statusCode;
    private Headers headers;

    public SlicingDice(String masterKey) {
        this._masterKey = masterKey;
        _timeout = 60;
    }

    public SlicingDice(String masterKey, String customKey, String writeKey, String readKey) {
        this._masterKey = masterKey;
        this._customKey = customKey;
        this._writeKey = writeKey;
        this._readKey = readKey;
        _timeout = 60;
    }

    public SlicingDice(String masterKey, String customKey, String writeKey, String readKey, int timeout) {
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

    private ArrayList<Object> getCurrentKey(){
        if (this._masterKey != null){
            ArrayList<Object> result = new ArrayList<Object>();
            result.add(this._masterKey);
            result.add(2);
            return result;
        } else if (this._customKey != null){
            ArrayList<Object> result = new ArrayList<Object>();
            result.add(this._customKey);
            result.add(2);
            return result;
        } else if (this._writeKey != null){
            ArrayList<Object> result = new ArrayList<Object>();
            result.add(this._writeKey);
            result.add(1);
            return result;
        } else if (this._readKey != null){
            ArrayList<Object> result = new ArrayList<Object>();
            result.add(this._readKey);
            result.add(0);
            return result;
        }
        throw new SlicingDiceKeyException("You need put a key.");
    }

    private String getKey(int levelKey) {
        List<Object> currentLevelKey = this.getCurrentKey();
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
    private JSONObject makeRequest(String url, JSONObject data, String reqType, int keyLevel)
            throws IOException {
        String apiKey = this.getKey(keyLevel);
        Response resp = null;
        if (reqType.equals("post")) {
            resp = Requester.post(url, data.toString(), apiKey, _timeout);
        } else if (reqType.equals("put")) {
            resp = Requester.put(url, data.toString(), apiKey, _timeout);
        } else if (reqType.equals("delete")) {
            resp = Requester.delete(url, apiKey, _timeout);
        }
        return handlerResponse(resp);
    }

    private JSONObject makeRequest(String url, int keyLevel)
            throws IOException {
        String apiKey = this.getKey(keyLevel);
        Response resp = Requester.get(url, apiKey, _timeout);
        return handlerResponse(resp);
    }

    private JSONObject handlerResponse(Response resp) throws IOException {
        HandlerResponse responseData = new HandlerResponse(resp.body().string(),
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
    private boolean checkRequest(Response response) throws SDHttpError {
        if (!response.isSuccessful()) {
            if (response.code() >= 400 && response.code() <= 499) {
                throw new SDHttpError("Client Error " + response.code() + "(" + response.message() + ")");
            }
            if (response.code() >= 500 && response.code() <= 600) {
                throw new SDHttpError("Server Error " + response.code() + "(" + response.message() + ")");
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
    private void populateResult(HandlerResponse response) throws IOException {
        this.statusCode = response.getStatusCode();
        this.headers = response.getHeaders();
    }

    private String wrapperTest(boolean test) throws IOException {
        if (test) return this.BaseURL + "/test";
        return this.BaseURL;
    }
    /**
     * Create field in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice field format
     * @return A String with json request result if your field is valid
     * @throws IOException
     */
    private JSONObject wrapperCreateField(JSONObject data, String url) throws IOException {
        FieldValidator fieldValidator = new FieldValidator(data);
        if (fieldValidator.validator()) {
            return this.makeRequest(url, data, "post", 2);
        }
        return null;
    }
    public JSONObject createField(JSONObject data) throws IOException {
        String url = BaseURL + URLResources.FIELD.url;
        return this.wrapperCreateField(data, url);
    }

    public JSONObject createField(JSONObject data, boolean test) throws IOException {
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
        String url = BaseURL + URLResources.FIELD.url;
        return this.makeRequest(url, 2);
    }

    public JSONObject getFields(boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.FIELD.url;
        return this.makeRequest(url, 2);
    }

    public JSONObject index(JSONObject data) throws IOException {
        String url = BaseURL + URLResources.INDEX.url;
        return this.makeRequest(url, data, "post", 1);
    }

    public JSONObject index(JSONObject data, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.INDEX.url;
        return this.makeRequest(url, data, "post", 1);
    }

    public JSONObject index(JSONObject data, boolean autoCreateFields, boolean test) throws IOException {
        data.put("auto-create-fields", autoCreateFields);
        String url = this.wrapperTest(test);
        url += URLResources.INDEX.url;
        return this.makeRequest(url, data, "post", 1);
    }

    /**
     * Make a count query in Slicing Dice
     *
     * @param url   A url to make request
     * @param query A JSONObject count query
     * @return A String with count query result
     * @throws IOException
     */
    private JSONObject countQueryWrapper(String url, JSONObject query) throws IOException {
        QueryCountValidator queryValidator = new QueryCountValidator(query);
        if (!queryValidator.validator()) {
            return null;
        }
        return this.makeRequest(url, query, "post", 0);
    }

    /**
     * Make a data extraction query in Slicing Dice
     *
     * @param url   A url to make request
     * @param query A JSONObject data extraction query
     * @return A String with data extraction query result
     * @throws IOException
     */
    private JSONObject dataExtractionWrapper(String url, JSONObject query) throws IOException {
        QueryDataExtractionValidator queryValidator = new QueryDataExtractionValidator(query);
        if (!queryValidator.validator()) {
            return null;
        }
        return this.makeRequest(url, query, "post", 0);
    }

    /**
     * Get all getProjects in your account.
     *
     * @return All getProjects(active and inactive).
     * @throws IOException
     */
    public JSONObject getProjects() throws IOException {
        String url = BaseURL + URLResources.PROJECT.url;
        return this.makeRequest(url, 2);
    }

    public JSONObject getProjects(boolean test) throws IOException {
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
    public JSONObject countEntity(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    public JSONObject countEntity(JSONObject query, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    public JSONObject countEntityTotal() throws IOException {
        String url = BaseURL + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        return this.makeRequest(url, 0);
    }
    public JSONObject countEntityTotal(boolean test) throws IOException {
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
    public JSONObject countEvent(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_COUNT_EVENT.url;
        return countQueryWrapper(url, query);
    }

    public JSONObject countEvent(JSONObject query, boolean test) throws IOException {
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
    private JSONObject wrapperAggregation(JSONObject query, String url) throws IOException {
        if (!query.has("query")) {
            throw new InvalidQueryException("The aggregation query must have up the key 'query'.");
        }
        if (query.length() > 5) {
            throw new MaxLimitException("The aggregation query must have up to 5 fields per request.");
        }
        return makeRequest(url, query, "post", 0);
    }

    public JSONObject aggregation(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_AGGREGATION.url;
        return this.wrapperAggregation(query, url);
    }

    public JSONObject aggregation(JSONObject query, boolean test) throws IOException {
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
    private JSONObject wrapperTopValues(JSONObject query, String url) throws IOException {
        TopValuesValidator topValuesValidator = new TopValuesValidator(query);
        if (topValuesValidator.validator())
            return makeRequest(url, query, "post", 0);
        return null;
    }

    public JSONObject topValues(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_TOP_VALUES.url;
        return this.wrapperTopValues(query, url);
    }

    public JSONObject topValues(JSONObject query, boolean test) throws IOException {
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
    private JSONObject wrapperExistsEntity(JSONArray ids, String url) throws IOException {
        if (ids.length() > 100) {
            throw new MaxLimitException("The query exists entity must have up to 100 ids.");
        }
        JSONObject query = new JSONObject()
                .put("ids", ids);
        return this.makeRequest(url, query, "post", 0);
    }
    public JSONObject existsEntity(JSONArray ids) throws IOException {
        String url = BaseURL + URLResources.QUERY_EXISTS_ENTITY.url;
        return this.wrapperExistsEntity(ids, url);
    }

    public JSONObject existsEntity(JSONArray ids, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_EXISTS_ENTITY.url;
        return this.wrapperExistsEntity(ids, url);
    }

    public JSONObject getSavedQuery(String queryName) throws IOException {
        String url = BaseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, 0);
    }
    public JSONObject getSavedQuery(String queryName, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, 0);
    }

    public JSONObject getSavedQueries() throws IOException {
        String url = BaseURL + URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, 2);
    }

    public JSONObject getSavedQueries(boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, 2);
    }

    public JSONObject deleteSavedQuery(String queryName) throws IOException {
        String url = BaseURL + URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, null, "delete", 2);
    }

    public JSONObject deleteSavedQuery(String queryName, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, null, "delete", 2);
    }
    /**
     * Create a saved query in Slicing Dice API
     *
     * @param query A JSONObject saved query
     * @return A String with saved query if request was successful
     * @throws IOException
     */
    private JSONObject wrapperCreateSavedQuery(JSONObject query, String url) throws IOException {
        if (query.has("name") && query.has("type") && query.has("query")) {
            String queryType = query.getString("type");
            if (!this.queryTypes.contains(queryType)) {
                throw new InvalidQueryException("The query saved has a invalid type.(" + queryType + ").");
            }
            return this.makeRequest(url, query, "post", 1);
        }
        return null;
    }
    public JSONObject createSavedQuery(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_SAVED.url;
        return this.wrapperCreateSavedQuery(query, url);
    }
    public JSONObject createSavedQuery(JSONObject query, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url;
        return this.wrapperCreateSavedQuery(query, url);
    }

    /**
     * Update a saved query in Slicing Dice API
     *
     * @param query A JSONObject saved query
     * @return A String with new saved query if request was successful
     * @throws IOException
     */
    public JSONObject updateSavedQuery(String queryName, JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, query, "put", 2);
    }

    public JSONObject updateSavedQuery(String queryName, JSONObject query, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, query, "put", 2);
    }

    /**
     * Make a data extraction score query in Slicing Dice API
     *
     * @param query A JSONObject data extraction score query
     * @return A String with data extraction score query result
     * @throws IOException
     */
    public JSONObject score(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_DATA_EXTRACTION_SCORE.url;
        return dataExtractionWrapper(url, query);
    }

    public JSONObject score(JSONObject query, boolean test) throws IOException {
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
    public JSONObject result(JSONObject query) throws IOException {
        String url = BaseURL + URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        return dataExtractionWrapper(url, query);
    }
    public JSONObject result(JSONObject query, boolean test) throws IOException {
        String url = this.wrapperTest(test);
        url += URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        return dataExtractionWrapper(url, query);
    }
}
