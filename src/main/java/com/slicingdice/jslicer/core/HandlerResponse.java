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

import com.slicingdice.jslicer.exceptions.api.FieldCreateInternalException;
import com.slicingdice.jslicer.exceptions.api.account.AccountBannedException;
import com.slicingdice.jslicer.exceptions.api.account.AccountDisabledException;
import com.slicingdice.jslicer.exceptions.api.account.AccountMissingPaymentMethodException;
import com.slicingdice.jslicer.exceptions.api.account.AccountPaymentRequiredException;
import com.slicingdice.jslicer.exceptions.api.request.RequestIncorrectContentTypeValueException;
import com.slicingdice.jslicer.exceptions.api.request.RequestInvalidJsonException;
import com.slicingdice.jslicer.exceptions.api.request.RequestMissingContentTypeException;
import com.slicingdice.jslicer.exceptions.api.request.RequestRateLimitException;
import com.slicingdice.jslicer.exceptions.api.InternalException;
import com.slicingdice.jslicer.exceptions.api.auth.*;
import com.slicingdice.jslicer.exceptions.api.field.*;
import com.slicingdice.jslicer.exceptions.api.index.*;
import com.slicingdice.jslicer.exceptions.api.query.*;
import okhttp3.Headers;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Find for Slicing Dice internal API errors in JSON result
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class HandlerResponse {

    private String result;
    private Headers headers;
    private int statusCode;

    public HandlerResponse(String result, Headers headers, int statusCode) {
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
    private void raiseError(JSONObject error) {
        int codeError = error.getInt("code");
        String message = error.getString("message");
        switch (codeError) {
            case 10:
                throw new AuthMissingHeaderException(message);
            case 11:
                throw new AuthAPIKeyException(message);
            case 12:
                throw new AuthInvalidAPIKeyException(message);
            case 13:
                throw new AuthIncorrectPermissionException(message);
            case 14:
                throw new AuthInvalidRemoteAddrException(message);
            case 16:
                throw new CustomKeyInvalidPermissionForFieldException(message);
            case 17:
                throw new CustomKeyInvalidOperationException(message);
            case 18:
                throw new CustomKeyNotPermittedException(message);
            case 19:
                throw new CustomKeyRouteNotPermittedException(message);
            // Request validations (20 - 29)
            case 21:
                throw new RequestMissingContentTypeException(message);
            case 22:
                throw new RequestIncorrectContentTypeValueException(message);
            case 23:
                throw new RequestRateLimitException(message);
            case 24:
                throw new RequestInvalidJsonException(message);
            // Account Errors (30 - 39)
            case 30:
                throw new AccountMissingPaymentMethodException(message);
            case 31:
                throw new AccountPaymentRequiredException(message);
            case 32:
                throw new AccountBannedException(message);
            case 33:
                throw new AccountDisabledException(message);
            // Field errors (40 - 59)
            case 40:
                throw new FieldMissingParamException(message);
            case 41:
                throw new FieldTypeException(message);
            case 42:
                throw new FieldIntegerValuesException(message);
            case 43:
                throw new FieldAlreadyExistsException(message);
            case 44:
                throw new FieldLimitException(message);
            case 45:
                throw new FieldTimeSeriesLimitException(message);
            case 46:
                throw new FieldTimeSeriesSystemLimitException(message);
            case 47:
                throw new FieldDecimalTypeException(message);
            case 48:
                throw new FieldStorageValueException(message);
            case 49:
                throw new FieldInvalidApiNameException(message);
            case 50:
                throw new FieldInvalidNameException(message);
            case 51:
                throw new FieldInvalidDescriptionException(message);
            case 53:
                throw new FieldInvalidCardinalityException(message);
            case 54:
                throw new FieldDecimalLimitException(message);
            case 55:
                throw new FieldRangeLimitException(message);
            // Index errors (60 - 79)
            case 60:
                throw new IndexEntityKeyTypeException(message);
            case 61:
                throw new IndexEntityValueTypeException(message);
            case 62:
                throw new IndexFieldNameTypeException(message);
            case 63:
                throw new IndexFieldTypeException(message);
            case 64:
                throw new IndexEntityNameTooBigException(message);
            case 65:
                throw new IndexFieldValueTooBigException(message);
            case 66:
                throw new IndexDateFormatException(message);
            case 67:
                throw new IndexFieldNotActiveException(message);
            case 68:
                throw new IndexIdLimitException(message);
            case 69:
                throw new IndexFieldLimitException(message);
            case 71:
                throw new IndexFieldStringEmptyValueException(message);
            case 72:
                throw new IndexFieldTimeSeriesInvalidParameterException(message);
            case 73:
                throw new IndexFieldNumericInvalidValueException(message);
            case 74:
                throw new IndexFieldTimeSeriesMissingValueException(message);
            case 75:
                throw new QueryTimeSeriesInvalidPrecisionSecondsException(message);
            case 76:
                throw new QueryTimeSeriesInvalidPrecisionMinutesException(message);
            case 77:
                throw new QueryTimeSeriesInvalidPrecisionHoursException(message);
            // Query errors (80 - 109)
            case 80:
                throw new QueryMissingQueryException(message);
            case 81:
                throw new QueryInvalidTypeException(message);
            case 82:
                throw new QueryMissingTypeParamException(message);
            case 83:
                throw new QueryInvalidOperatorException(message);
            case 84:
                throw new QueryIncorrectOperatorUsageException(message);
            case 85:
                throw new QueryFieldNotActiveException(message);
            case 86:
                throw new QueryMissingOperatorException(message);
            case 87:
                throw new QueryIncompleteException(message);
            case 88:
                throw new QueryEventCountQueryException(message);
            case 89:
                throw new QueryDateFormatException(message);
            case 90:
                throw new QueryIntegerException(message);
            case 91:
                throw new QueryFieldLimitException(message);
            case 92:
                throw new QueryLevelLimitException(message);
            case 93:
                throw new QueryBadAggsFormationException(message);
            case 94:
                throw new QueryInvalidAggFilterException(message);
            case 95:
                throw new QueryMetricsLevelException(message);
            case 96:
                throw new QueryTimeSeriesException(message);
            case 97:
                throw new QueryMetricsTypeException(message);
            case 98:
                throw new QueryContainsNumericException(message);
            case 99:
                throw new QueryExistsEntityLimitException(message);
            case 100:
                throw new QueryMultipleFiltersException(message);
            case 101:
                throw new QueryContainsValueTypeException(message);
            case 102:
                throw new QueryMissingNameParamException(message);
            case 103:
                throw new QuerySavedAlreadyExistsException(message);
            case 104:
                throw new QuerySavedNotExistsException(message);
            case 105:
                throw new QuerySavedInvalidTypeException(message);
            case 106:
                throw new MethodNotAllowedException(message);
            case 107:
                throw new QueryExistsMissingIdsException(message);
            case 108:
                throw new QueryInvalidFormatException(message);
            case 109:
                throw new QueryTopValuesParameterEmptyException(message);
            case 110:
                throw new QueryDataExtractionLimitValueException(message);
            case 111:
                throw new QueryDataExtractionLimitValueTooBigException(message);
            case 112:
                throw new QueryDataExtractionLimitAndPageTokenValueException(message);
            case 113:
                throw new QueryDataExtractionPageTokenValueException(message);
            case 114:
                throw new QueryDataExtractionFieldLimitException(message);
            case 115:
                throw new QueryExistsEntityEmptyException(message);
            case 116:
                throw new QuerySavedInvalidQueryValueException(message);
            case 117:
                throw new QuerySavedInvalidCachePeriodValueException(message);
            case 118:
                throw new QuerySavedInvalidNameException(message);
            case 119:
                throw new QueryCountInvalidParameterException(message);
            case 120:
                throw new QueryAggregationInvalidParameterException(message);
            case 121:
                throw new QueryAggregationInvalidFilterQueryException(message);
            // Internal errors (130 - 140)
            case 130:
                throw new InternalException(message);
            case 131:
                throw new FieldCreateInternalException(message);
        }
    }

    /**
     * Check if request was a successful
     *
     * @return true if the JSON result don't have errors
     */
    public boolean requestSuccessful() {
        JSONObject data;

        try {
            data = new JSONObject(this.result);
        } catch (JSONException exception) {
            throw new InternalException("SlicingDice: Internal error.");
        }

        if (data.has("errors")) {
            JSONObject error = data.getJSONArray("errors").getJSONObject(0);
            this.raiseError(error);
        }
        return true;
    }
}
