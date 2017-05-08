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

import com.slicingdice.jslicer.exceptions.client.MaxLimitException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Slicing Dice validator to query JSONObject.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class TopValuesValidator {

    /**
     * A String list with all operations supported
     */
    private final JSONObject data;

    public TopValuesValidator(final JSONObject data) {
        this.data = data;
    }

    /**
     * Check if the queries in query exceeds limits of Slicing Dice API
     * @return true if exceeds query limit and false otherwise
     */
    private boolean exceedsQueriesLimit() {
        return this.data.length() > 5;
    }

    /**
     * Check if the columns in query exceeds limits per request of Slicing Dice API
     * @return false if not exceeds column limit
     */
    private boolean exceedsColumnsLimit() throws MaxLimitException {
        final Iterator<?> keys = this.data.keys();
        while (keys.hasNext()) {
            final String key = (String) keys.next();
            if (this.data.getJSONObject(key).length() > 6) {
                throw new MaxLimitException("The query " +
                        "exceeds the limit of columns per query in request");
            }
        }
        return false;
    }

    /**
     * Check if the contains values in query exceeds limits of Slicing Dice API
     * @return false if not exceeds contains limit
     */
    private boolean exceedsValuesContainsLimit() throws MaxLimitException {
        final Iterator<?> keys = this.data.keys();
        while (keys.hasNext()) {
            final String key = (String) keys.next();
            final JSONObject queryColumn = this.data.getJSONObject(key);
            if (queryColumn.has("contains")) {
                if (queryColumn.getJSONArray("contains").length() > 5) {
                    throw new MaxLimitException("The query " +
                            "exceeds the limit of contains per query in request");
                }
            }
        }
        return false;
    }

    /**
     * Validate top values query
     * @return true if top values query is valid and false otherwise
     */
    public boolean validator() {
        return !exceedsQueriesLimit() && !exceedsColumnsLimit() && !exceedsValuesContainsLimit();
    }
}
