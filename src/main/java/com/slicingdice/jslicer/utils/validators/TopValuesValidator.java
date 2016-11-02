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
     * A String list with all types of query supported
     */
    private List<String> queryTypes;

    /**
     * A String list with all operations supported
     */
    private JSONObject data;

    public TopValuesValidator(JSONObject data) {
        this.data = data;
    }

    /**
     * Check if the queries in query exceeds limits of Slicing Dice API
     */
    private boolean exceedsQueriesLimit() {
        if (this.data.length() > 5)
            return true;
        return false;
    }

    /**
     * Check if the fields in query exceeds limits per request of Slicing Dice API
     */
    private boolean exceedsFieldsLimit() {
        Iterator<?> keys = this.data.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (this.data.getJSONObject(key).length() > 6) {
                throw new MaxLimitException("The query " + "exceeds the limit of fields per query in request");
            }
        }
        return false;
    }

    /**
     * Check if the contains values in query exceeds limits of Slicing Dice API
     */
    private boolean exceedsValuesContainsLimit() {
        Iterator<?> keys = this.data.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            JSONObject queryField = this.data.getJSONObject(key);
            if (queryField.has("contains")) {
                if (queryField.getJSONArray("contains").length() > 5) {
                    throw new MaxLimitException("The query " + "exceeds the limit of contains per query in request");
                }
            }
        }
        return false;
    }

    public boolean validator() {
        if (!exceedsQueriesLimit() && !exceedsFieldsLimit() && !exceedsValuesContainsLimit())
            return true;
        return false;
    }
}
