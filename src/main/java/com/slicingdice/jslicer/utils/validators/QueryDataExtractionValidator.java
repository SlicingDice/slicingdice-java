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
package com.slicingdice.jslicer.utils.validators;

import com.slicingdice.jslicer.exceptions.client.InvalidQueryException;
import com.slicingdice.jslicer.exceptions.client.MaxLimitException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Slicing Dice validator to query JSONObject.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class QueryDataExtractionValidator {
    private final JSONObject data;

    public QueryDataExtractionValidator(final JSONObject data) {
        this.data = data;
    }

    /**
     * Check if all keys in JSONObject are valid.
     * @return true if all keys in JSONObject are valid and false otherwise
     */
    private boolean validKeys() {
        final Iterator<?> keys = this.data.keys();
        while (keys.hasNext()) {
            final String key = (String) keys.next();
            if (key.equals("limit")) {
                if (!(this.data.get(key) instanceof Integer)) {
                    throw new InvalidQueryException("The key 'limit' in query has a invalid value.");
                }
            } else if (key.equals("columns")) {
                final Object columns = this.data.get("columns");
                if (columns instanceof JSONArray) {
                    if (((JSONArray) columns).length() > 10) {
                        throw new MaxLimitException("The key 'columns' in data extraction result" +
                                " must have up to 10 columns.");
                    }
                } else if (columns instanceof String) {
                    if (!columns.equals("all")) {
                        throw new InvalidQueryException("The key 'columns' should be a list of" +
                                " columns or the keyword 'all'");
                    }
                } else {
                    throw new InvalidQueryException("The key 'columns' should be a list of" +
                            " columns or the keyword 'all'");
                }
            }
        }
        return true;
    }

    /**
     * Validate data extraction query
     * @return true if data extraction query is valid and false otherwise
     */
    public boolean validator() {
        return this.validKeys();
    }
}
