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

/**
 * Slicing Dice validator to query JSONObject.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class QueryCountValidator {

    /**
     * A String list with all operations supported
     */
    private final JSONObject data;

    public QueryCountValidator(final JSONObject data) {
        this.data = data;
    }

    /**
     * Validate the count query
     * @return true if count query is valid and false otherwise
     */
    public boolean validator() {
        int querySize = this.data.length();

        // bypass-cache property should not be considered as query;
        if (this.data.has("bypass-cache")) {
            querySize--;
        }

        if (querySize > 10) {
            throw new MaxLimitException(
                    "The query count entity has a limit of 10 queries by request.");
        }

        return true;
    }
}
