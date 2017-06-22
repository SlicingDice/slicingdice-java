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
package com.slicingdice.jslicer.core;

import com.slicingdice.jslicer.exceptions.DemoUnavailableException;
import com.slicingdice.jslicer.exceptions.IndexColumnsLimitException;
import com.slicingdice.jslicer.exceptions.IndexEntitiesLimitException;
import com.slicingdice.jslicer.exceptions.RequestBodySizeExceededException;
import com.slicingdice.jslicer.exceptions.RequestRateLimitException;
import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import com.slicingdice.jslicer.exceptions.api.InternalException;
import okhttp3.Headers;
import org.json.JSONObject;
import org.json.JSONException;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.logging.Logger;

/**
 * Find for Slicing Dice internal API errors in JSON result
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class HandlerResponse {

    private static final Logger logger = Logger.getLogger(HandlerResponse.class.getCanonicalName());

    private final String result;
    private final Headers headers;
    private final int statusCode;

    public HandlerResponse(final String result, final Headers headers, final int statusCode) {
        this.result = result;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public String getResult() {
        return this.result;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    /**
     * Raise Slicing Dice API errors
     *
     * @param error A JSONObject with values from key 'errors'
     */
    private void raiseError(final JSONObject error) throws SlicingDiceException {
        final int codeError = error.getInt("code");

        switch (codeError) {
            case 2:
                throw new DemoUnavailableException(error);
            case 1502:
                throw new RequestRateLimitException(error);
            case 1507:
                throw new RequestBodySizeExceededException(error);
            case 2012:
                throw new IndexEntitiesLimitException(error);
            case 2013:
                throw new IndexColumnsLimitException(error);
            default:
                throw new SlicingDiceException(error);
        }
    }

    /**
     * Check if request was a successful
     *
     * @return true if the JSON result don't have errors
     */
    public boolean requestSuccessful() throws SlicingDiceException {
        final JSONObject data;

        try {
            data = new JSONObject(this.result);
        } catch (final JSONException exception) {
            logger.severe(String.format("Couldn't parse JSON '%s'", this.result));
            throw new InternalException("SlicingDice: Error while parsing JSON.", exception);
        }

        if (data.has("errors")) {
            final JSONObject error = data.getJSONArray("errors").getJSONObject(0);
            this.raiseError(error);
        }

        return true;
    }
}
