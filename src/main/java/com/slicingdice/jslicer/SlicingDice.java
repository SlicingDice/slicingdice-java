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

import com.slicingdice.jslicer.core.HandlerResponse;
import com.slicingdice.jslicer.core.Requester;
import com.slicingdice.jslicer.exceptions.client.InvalidQueryException;
import com.slicingdice.jslicer.exceptions.client.MaxLimitException;
import com.slicingdice.jslicer.exceptions.client.SlicingDiceKeyException;
import com.slicingdice.jslicer.utils.URLResources;
import com.slicingdice.jslicer.utils.validators.ColumnValidator;
import com.slicingdice.jslicer.utils.validators.QueryCountValidator;
import com.slicingdice.jslicer.utils.validators.QueryDataExtractionValidator;
import com.slicingdice.jslicer.utils.validators.TopValuesValidator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;


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
    private String baseURL = (System.getenv("SD_API_ADDRESS") != null) ?
            System.getenv("SD_API_ADDRESS") : "https://api.slicingdice.com/v1";

    public SlicingDice(final String masterKey) {
        this.masterKey = masterKey;
        this.timeout = 60;
    }

    public SlicingDice(final String masterKey, final String customKey, final String writeKey,
                       final String readKey) {
        this.masterKey = masterKey;
        this.customKey = customKey;
        this.writeKey = writeKey;
        this.readKey = readKey;
        this.timeout = 60;
    }

    public SlicingDice(final String masterKey, final String customKey, final String writeKey,
                       final String readKey, final int timeout) {
        this.masterKey = masterKey;
        this.customKey = customKey;
        this.writeKey = writeKey;
        this.readKey = readKey;
        this.timeout = timeout;
    }

    /**
     * Call this to close executors
     */
    public void close() throws IOException {
        Requester.close();
    }

    @Deprecated
    public void setBaseURL(final String baseURL) {
        this.baseURL = baseURL;
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
     * Effectively makes the request, will return A JSONObject with json request result
     *
     * @param url  A url String to make request
     * @param data An Object to send in request
     * @return a future to deal with the query
     */
    private Future<Response> makeRequest(final String url, final Object data, final String reqType,
                                         final int keyLevel) {
        final String apiKey = this.getKey(keyLevel);

        if (reqType.equals(POST)) {
            return Requester.post(url, data.toString(), apiKey, timeout);
        } else if (reqType.equals(PUT)) {
            return Requester.put(url, data.toString(), apiKey, timeout);
        } else if (reqType.equals(DELETE)) {
            return Requester.delete(url, apiKey, timeout);
        }

        return null;
    }

    /**
     * Effectively makes the request, will return A JSONObject with json request result
     *
     * @param url     A url String to make request
     * @param data    An Object to send in request
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void makeRequest(final String url, final Object data, final String reqType,
                             final int keyLevel, final HandlerResponse handler) {
        final String apiKey = this.getKey(keyLevel);

        if (reqType.equals(POST)) {
            Requester.post(url, data.toString(), apiKey, timeout, handler);
        } else if (reqType.equals(PUT)) {
            Requester.put(url, data.toString(), apiKey, timeout, handler);
        } else if (reqType.equals(DELETE)) {
            Requester.delete(url, apiKey, timeout, handler);
        }
    }

    private Future<Response> makeRequest(final String url, final int keyLevel) {
        final String apiKey = this.getKey(keyLevel);
        return Requester.get(url, apiKey, timeout);
    }

    private void makeRequest(final String url, final int keyLevel,
                             final HandlerResponse handler) {
        final String apiKey = this.getKey(keyLevel);
        Requester.get(url, apiKey, timeout, handler);
    }

    /**
     * Create column in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice column format
     * @return a future to get SlicingDice request result
     */
    private Future<Response> wrapperCreateColumn(final Object data, final String url) {
        final ColumnValidator columnValidator = new ColumnValidator(data);

        if (columnValidator.validator()) {
            return this.makeRequest(url, data, POST, 2);
        } else {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
    }

    /**
     * Create column in Slicing Dice
     *
     * @param data A JSONObject in the Slicing Dice column format
     */
    private void wrapperCreateColumn(final Object data, final String url,
                                     final HandlerResponse handler) {
        final ColumnValidator columnValidator = new ColumnValidator(data);

        if (columnValidator.validator()) {
            this.makeRequest(url, data, POST, 2, handler);
        } else {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
    }

    /**
     * Create column in Slicing Dice, will return the SlicingDice response
     *
     * @param data A JSONObject in the Slicing Dice column format
     * @return a future to get SlicingDice request result
     */
    public Future<Response> createColumn(final JSONObject data) {
        final String url = this.baseURL + URLResources.COLUMN.url;
        return this.wrapperCreateColumn(data, url);
    }

    /**
     * Create column in Slicing Dice, will return the SlicingDice response
     *
     * @param data    A JSONObject in the Slicing Dice column format
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void createColumn(final JSONObject data, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.COLUMN.url;
        this.wrapperCreateColumn(data, url, handler);
    }

    /**
     * Create column in Slicing Dice, will return the SlicingDice response
     *
     * @param dataArray A JSONArray with many JSONObjects in the Slicing Dice column format
     * @return a future to get SlicingDice request result
     */
    public Future<Response> createColumn(final JSONArray dataArray) {
        final String url = this.baseURL + URLResources.COLUMN.url;
        return this.wrapperCreateColumn(dataArray, url);
    }

    /**
     * Create column in Slicing Dice, will return the SlicingDice response
     *
     * @param dataArray A JSONArray with many JSONObjects in the Slicing Dice column format
     * @param handler   A handler that will call onError or onSuccess when the request finishes
     */
    public void createColumn(final JSONArray dataArray, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.COLUMN.url;
        this.wrapperCreateColumn(dataArray, url, handler);
    }

    /**
     * Get all columns, will return all columns(active and inactive).
     *
     * @return a future to get SlicingDice request result
     */
    public Future<Response> getColumns() {
        final String url = this.baseURL + URLResources.COLUMN.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Get all columns, will return all columns(active and inactive).
     *
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void getColumns(final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.COLUMN.url;
        this.makeRequest(url, 2, handler);
    }

    /**
     * Insert data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /insert. Will return the SlicingDice response.
     *
     * @param data A JSON object in the SlicingDice insert format
     * @return a future to get SlicingDice request result
     */
    public Future<Response> insert(final JSONObject data) {
        final String url = this.baseURL + URLResources.INSERT.url;
        return this.makeRequest(url, data, POST, 1);
    }

    /**
     * Insert data to existing entities or create new entities, if necessary. This method corresponds
     * to a POST request at /insert. Will return the SlicingDice response.
     *
     * @param data    A JSON object in the SlicingDice insert format
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void insert(final JSONObject data, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.INSERT.url;
        this.makeRequest(url, data, POST, 1, handler);
    }

    /**
     * Make a count query in Slicing Dice, will return a JSONObject with count query result
     *
     * @param url   A url to make request
     * @param query A JSONObject count query
     * @return a future to get SlicingDice request result
     */
    private Future<Response> countQueryWrapper(final String url, final JSONObject query) {
        final QueryCountValidator queryValidator = new QueryCountValidator(query);
        if (!queryValidator.validator()) {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a count query in Slicing Dice, will return a JSONObject with count query result
     *
     * @param url     A url to make request
     * @param query   A JSONObject count query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void countQueryWrapper(final String url, final JSONObject query,
                                   final HandlerResponse handler) {
        final QueryCountValidator queryValidator = new QueryCountValidator(query);
        if (!queryValidator.validator()) {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
        this.makeRequest(url, query, POST, 0, handler);
    }

    /**
     * Make a count query in Slicing Dice, will return a JSONObject with count query result
     *
     * @param url   A url to make request
     * @param query A JSONArray count query
     * @return a future to get SlicingDice request result
     */
    private Future<Response> countQueryWrapper(final String url, final JSONArray query) {
        final QueryCountValidator queryValidator = new QueryCountValidator(query);
        if (!queryValidator.validator()) {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a count query in Slicing Dice, will return a JSONObject with count query result
     *
     * @param url     A url to make request
     * @param query   A JSONArray count query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void countQueryWrapper(final String url, final JSONArray query,
                                   final HandlerResponse handler) {
        final QueryCountValidator queryValidator = new QueryCountValidator(query);
        if (!queryValidator.validator()) {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
        this.makeRequest(url, query, POST, 0, handler);
    }

    /**
     * Make a data extraction query in Slicing Dice
     *
     * @param url   A url to make request
     * @param query A JSONObject data extraction query
     * @return a future to get SlicingDice request result
     */
    private Future<Response> dataExtractionWrapper(final String url, final JSONObject query) {
        final QueryDataExtractionValidator queryValidator = new QueryDataExtractionValidator(query);
        if (!queryValidator.validator()) {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a data extraction query in Slicing Dice
     *
     * @param url     A url to make request
     * @param query   A JSONObject data extraction query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void dataExtractionWrapper(final String url, final JSONObject query,
                                       final HandlerResponse handler) {
        final QueryDataExtractionValidator queryValidator = new QueryDataExtractionValidator(query);
        if (!queryValidator.validator()) {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
        this.makeRequest(url, query, POST, 0, handler);
    }

    /**
     * Get information about current database, will return A JSONObject containing properties
     * of the current database.
     *
     * @return a future to get SlicingDice request result
     */
    public Future<Response> getDatabase() {
        final String url = this.baseURL + URLResources.DATABASE.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Get information about current database, will return A JSONObject containing properties
     * of the current database.
     *
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void getDatabase(final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.DATABASE.url;
        this.makeRequest(url, 2, handler);
    }

    /**
     * Make a count entity query in Slicing Dice API, will return a JSONObject with count entity query result
     *
     * @param query A JSONObject count entity query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> countEntity(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a count entity query in Slicing Dice API, will return a JSONObject with count entity query result
     *
     * @param query   A JSONObject count entity query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void countEntity(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY.url;
        countQueryWrapper(url, query, handler);
    }

    /**
     * Make a count entity query in Slicing Dice API, will return a JSONObject with count entity query result
     *
     * @param query A JSONArray count entity query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> countEntity(final JSONArray query) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a count entity query in Slicing Dice API, will return a JSONObject with count entity query result
     *
     * @param query   A JSONArray count entity query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void countEntity(final JSONArray query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY.url;
        countQueryWrapper(url, query, handler);
    }

    /**
     * Make a total query in Slicing Dice API, will return a JSONObject with total query result
     *
     * @return a future to get SlicingDice request result
     */
    public Future<Response> countEntityTotal() {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        return this.makeRequest(url, new JSONObject(), POST, 0);
    }

    /**
     * Make a total query in Slicing Dice API, will return a JSONObject with total query result
     *
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void countEntityTotal(final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        this.makeRequest(url, new JSONObject(), POST, 0, handler);
    }

    /**
     * Make a total query in Slicing Dice API, will return a JSONObject with total query result
     *
     * @param tables A Collection containing the tables in which the total query will be performed
     * @return a future to get SlicingDice request result
     */
    public Future<Response> countEntityTotal(final Collection<String> tables) {
        final JSONObject query = new JSONObject();
        query.put("tables", tables);

        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a total query in Slicing Dice API, will return a JSONObject with total query result
     *
     * @param tables  A Collection containing the tables in which the total query will be performed
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void countEntityTotal(final Collection<String> tables,
                                 final HandlerResponse handler) {
        final JSONObject query = new JSONObject();
        query.put("tables", tables);

        final String url = this.baseURL + URLResources.QUERY_COUNT_ENTITY_TOTAL.url;
        this.makeRequest(url, query, POST, 0, handler);
    }

    /**
     * Make a count event query in Slicing Dice API, will return a JSONObject with count event query result
     *
     * @param query A JSONObject count event query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> countEvent(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_EVENT.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a count event query in Slicing Dice API, will return a JSONObject with count event query result
     *
     * @param query   A JSONObject count event query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void countEvent(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_EVENT.url;
        countQueryWrapper(url, query, handler);
    }

    /**
     * Make a count event query in Slicing Dice API, will return a JSONObject with count event query result
     *
     * @param query A JSONArray count event query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> countEvent(final JSONArray query) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_EVENT.url;
        return countQueryWrapper(url, query);
    }

    /**
     * Make a count event query in Slicing Dice API, will return a JSONObject with count event query result
     *
     * @param query   A JSONArray count event query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void countEvent(final JSONArray query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_COUNT_EVENT.url;
        countQueryWrapper(url, query, handler);
    }

    /**
     * Make a aggregation query in Slicing Dice API, will return a JSONObject with aggregation query result
     *
     * @param query A JSONObject aggregation query
     * @return a future to get SlicingDice request result
     */
    private Future<Response> wrapperAggregation(final JSONObject query, final String url) {
        if (!query.has("query")) {
            throw new InvalidQueryException("The aggregation query must have up the key 'query'.");
        }
        if (query.length() > 5) {
            throw new MaxLimitException("The aggregation query must have up to 5 columns per request.");
        }
        return makeRequest(url, query, POST, 0);
    }

    /**
     * Make a aggregation query in Slicing Dice API, will return a JSONObject with aggregation query result
     *
     * @param query   A JSONObject aggregation query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void wrapperAggregation(final JSONObject query, final String url,
                                    final HandlerResponse handler) {
        if (!query.has("query")) {
            throw new InvalidQueryException("The aggregation query must have up the key 'query'.");
        }
        if (query.length() > 5) {
            throw new MaxLimitException("The aggregation query must have up to 5 columns per request.");
        }
        makeRequest(url, query, POST, 0, handler);
    }

    /**
     * Make a aggregation query in Slicing Dice API, will return a JSONObject with aggregation query result
     *
     * @param query A JSONObject aggregation query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> aggregation(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_AGGREGATION.url;
        return this.wrapperAggregation(query, url);
    }

    /**
     * Make a aggregation query in Slicing Dice API, will return a JSONObject with aggregation query result
     *
     * @param query   A JSONObject aggregation query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void aggregation(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_AGGREGATION.url;
        this.wrapperAggregation(query, url, handler);
    }

    /**
     * Make a top values query in Slicing Dice API, will return a JSONObject with top values query result
     *
     * @param query A JSONObject top values query
     * @return a future to get SlicingDice request result
     */
    private Future<Response> wrapperTopValues(final JSONObject query, final String url) {
        final TopValuesValidator topValuesValidator = new TopValuesValidator(query);
        if (topValuesValidator.validator()) {
            return makeRequest(url, query, POST, 0);
        } else {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
    }

    /**
     * Make a top values query in Slicing Dice API, will return a JSONObject with top values query result
     *
     * @param query   A JSONObject top values query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void wrapperTopValues(final JSONObject query, final String url,
                                  final HandlerResponse handler) {
        final TopValuesValidator topValuesValidator = new TopValuesValidator(query);
        if (topValuesValidator.validator()) {
            makeRequest(url, query, POST, 0, handler);
        } else {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
    }

    /**
     * Make a top values query in Slicing Dice API, will return a JSONObject with top values query result
     *
     * @param query A JSONObject top values query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> topValues(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_TOP_VALUES.url;
        return this.wrapperTopValues(query, url);
    }

    /**
     * Make a top values query in Slicing Dice API, will return a JSONObject with top values query result
     *
     * @param query   A JSONObject top values query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void topValues(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_TOP_VALUES.url;
        this.wrapperTopValues(query, url, handler);
    }

    /**
     * Make a exists entity query in Slicing Dice API, will return a JSONObject with exists entity query result
     *
     * @param ids   A JSONArray exists entity query
     * @param table In which table entities check be checked
     * @return a future to get SlicingDice request result
     */
    private Future<Response> wrapperExistsEntity(final JSONArray ids, final String table,
                                                 final String url)
            throws MaxLimitException {
        if (ids.length() > 100) {
            throw new MaxLimitException("The query exists entity must have up to 100 ids.");
        }
        final JSONObject query = new JSONObject().put("ids", ids);
        if (table != null) {
            query.put("table", table);
        }
        return this.makeRequest(url, query, POST, 0);
    }

    /**
     * Make a exists entity query in Slicing Dice API, will return a JSONObject with exists entity query result
     *
     * @param ids     A JSONArray exists entity query
     * @param table   In which table entities check be checked
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void wrapperExistsEntity(final JSONArray ids, final String table,
                                     final String url, final HandlerResponse handler)
            throws MaxLimitException {
        if (ids.length() > 100) {
            throw new MaxLimitException("The query exists entity must have up to 100 ids.");
        }
        final JSONObject query = new JSONObject().put("ids", ids);
        if (table != null) {
            query.put("table", table);
        }
        this.makeRequest(url, query, POST, 0, handler);
    }

    /**
     * Make a exists entity query in Slicing Dice API, will return A JSONObject with exists entity query result
     *
     * @param ids   A JSONArray exists entity query
     * @param table In which table entities check be checked
     * @return a future to get SlicingDice request result
     */
    public Future<Response> existsEntity(final JSONArray ids, final String table) {
        final String url = this.baseURL + URLResources.QUERY_EXISTS_ENTITY.url;
        return this.wrapperExistsEntity(ids, table, url);
    }

    /**
     * Make a exists entity query in Slicing Dice API, will return A JSONObject with exists entity query result
     *
     * @param ids     A JSONArray exists entity query
     * @param table   In which table entities check be checked
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void existsEntity(final JSONArray ids, final String table,
                             final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_EXISTS_ENTITY.url;
        this.wrapperExistsEntity(ids, table, url, handler);
    }

    /**
     * Make a exists entity query in Slicing Dice API, will return A JSONObject with exists entity query result
     *
     * @param ids A JSONArray exists entity query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> existsEntityWithoutTable(final JSONArray ids) {
        return this.existsEntity(ids, null);
    }

    /**
     * Make a exists entity query in Slicing Dice API, will return A JSONObject with exists entity query result
     *
     * @param ids     A JSONArray exists entity query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void existsEntityWithoutTable(final JSONArray ids, final HandlerResponse handler) {
        this.existsEntity(ids, null, handler);
    }

    /**
     * Query SlicingDice API for saved queries, will return JSONObject with get saved query result
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @return a future to get SlicingDice request result
     */
    public Future<Response> getSavedQuery(final String queryName) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, 0);
    }

    /**
     * Query SlicingDice API for saved queries, will return JSONObject with get saved query result
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param handler   A handler that will call onError or onSuccess when the request finishes
     */
    public void getSavedQuery(final String queryName, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        this.makeRequest(url, 0, handler);
    }

    /**
     * Query SlicingDice API for all saved queries, will return a JSONObject with get saved query result
     *
     * @return a future to get SlicingDice request result
     */
    public Future<Response> getSavedQueries() {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url;
        return this.makeRequest(url, 2);
    }

    /**
     * Query SlicingDice API for all saved queries, will return a JSONObject with get saved query result
     */
    public void getSavedQueries(final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url;
        this.makeRequest(url, 2, handler);
    }

    /**
     * Delete a previous saved query on SlicingDice API, will return a JSONObject with get saved query result
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @return a future to get SlicingDice request result
     */
    public Future<Response> deleteSavedQuery(final String queryName) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, null, DELETE, 2);
    }

    /**
     * Delete a previous saved query on SlicingDice API, will return a JSONObject with get saved query result
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param handler   A handler that will call onError or onSuccess when the request finishes
     */
    public void deleteSavedQuery(final String queryName, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        this.makeRequest(url, null, DELETE, 2, handler);
    }

    /**
     * Create a saved query in Slicing Dice API, return a JSONObject with saved query if request was successful
     *
     * @param query A JSONObject saved query
     * @return a future to get SlicingDice request result
     */
    private Future<Response> wrapperCreateSavedQuery(final JSONObject query, final String url) {
        if (query.has("name") && query.has("type") && query.has("query")) {
            final String queryType = query.getString("type");
            if (!this.queryTypes.contains(queryType)) {
                throw new InvalidQueryException("The query saved has a invalid type.(" + queryType
                        + ").");
            }
            return this.makeRequest(url, query, POST, 1);
        } else {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
    }

    /**
     * Create a saved query in Slicing Dice API, return a JSONObject with saved query if request was successful
     *
     * @param query   A JSONObject saved query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    private void wrapperCreateSavedQuery(final JSONObject query, final String url,
                                         final HandlerResponse handler) {
        if (query.has("name") && query.has("type") && query.has("query")) {
            final String queryType = query.getString("type");
            if (!this.queryTypes.contains(queryType)) {
                throw new InvalidQueryException("The query saved has a invalid type.(" + queryType
                        + ").");
            }
            this.makeRequest(url, query, POST, 1, handler);
        } else {
            throw new InvalidQueryException("Invalid query, please check the docs");
        }
    }

    /**
     * Create a saved query in Slicing Dice API, will return a JSONObject with saved query if request was successful
     *
     * @param query A JSONObject saved query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> createSavedQuery(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url;
        return this.wrapperCreateSavedQuery(query, url);
    }

    /**
     * Create a saved query in Slicing Dice API, will return a JSONObject with saved query if request was successful
     *
     * @param query   A JSONObject saved query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void createSavedQuery(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url;
        this.wrapperCreateSavedQuery(query, url, handler);
    }

    /**
     * Update a saved query in Slicing Dice API, will return a JSONObject with new saved query if request was successful
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param query     A JSONObject saved query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> updateSavedQuery(final String queryName, final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        return this.makeRequest(url, query, PUT, 2);
    }

    /**
     * Update a saved query in Slicing Dice API, will return a JSONObject with new saved query if request was successful
     *
     * @param queryName the name of the saved query that you want to retrieve
     * @param query     A JSONObject saved query
     * @param handler   A handler that will call onError or onSuccess when the request finishes
     */
    public void updateSavedQuery(final String queryName, final JSONObject query,
                                 final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_SAVED.url + queryName;
        this.makeRequest(url, query, PUT, 2, handler);
    }

    /**
     * Make a data extraction score query in Slicing Dice API, will return a JSONObject with data extraction score query result
     *
     * @param query A JSONObject data extraction score query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> score(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_DATA_EXTRACTION_SCORE.url;
        return dataExtractionWrapper(url, query);
    }

    /**
     * Make a data extraction score query in Slicing Dice API, will return a JSONObject with data extraction score query result
     *
     * @param query   A JSONObject data extraction score query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void score(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_DATA_EXTRACTION_SCORE.url;
        dataExtractionWrapper(url, query, handler);
    }

    /**
     * Make a data extraction result query in Slicing Dice API, will return a JSONObject with result of data extraction result query
     *
     * @param query A JSONObject data extraction result query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> result(final JSONObject query) {
        final String url = this.baseURL + URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        return dataExtractionWrapper(url, query);
    }

    /**
     * Make a data extraction result query in Slicing Dice API, will return a JSONObject with result of data extraction result query
     *
     * @param query   A JSONObject data extraction result query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void result(final JSONObject query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_DATA_EXTRACTION_RESULT.url;
        dataExtractionWrapper(url, query, handler);
    }

    /**
     * Make a sql query query in Slicing Dice API, will return a JSONObject with result of sql query
     *
     * @param query A JSONObject data extraction result query
     * @return a future to get SlicingDice request result
     */
    public Future<Response> sql(final String query) {
        final String url = this.baseURL + URLResources.QUERY_SQL.url;
        final String apiKey = this.getKey(0);
        return Requester.post(url, query, apiKey, timeout, true);
    }

    /**
     * Make a sql query query in Slicing Dice API, will return a JSONObject with result of sql query
     *
     * @param query   A SQL query
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public void sql(final String query, final HandlerResponse handler) {
        final String url = this.baseURL + URLResources.QUERY_SQL.url;
        final String apiKey = this.getKey(0);
        Requester.post(url, query, apiKey, timeout, handler, true);
    }

}
