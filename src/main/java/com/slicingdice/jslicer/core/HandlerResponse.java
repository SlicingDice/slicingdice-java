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

import com.slicingdice.jslicer.exceptions.api.ColumnCreateInternalException;
import com.slicingdice.jslicer.exceptions.api.account.*;
import com.slicingdice.jslicer.exceptions.api.request.*;
import com.slicingdice.jslicer.exceptions.api.InternalException;
import com.slicingdice.jslicer.exceptions.api.auth.*;
import com.slicingdice.jslicer.exceptions.api.column.*;
import com.slicingdice.jslicer.exceptions.api.insert.*;
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
    private void raiseError(final JSONObject error) {
        final int codeError = error.getInt("code");
        final String message = error.getString("message");
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
            case 15:
                throw new CustomKeyInvalidColumnCreationException(message);
            case 16:
                throw new CustomKeyInvalidPermissionForColumnException(message);
            case 17:
                throw new CustomKeyInvalidOperationException(message);
            case 18:
                throw new CustomKeyNotPermittedException(message);
            case 19:
                throw new CustomKeyRouteNotPermittedException(message);
            case 20:
                throw new DemoApiInvalidEndpointException(message);
            // Request validations (21 - 29)
            case 21:
                throw new RequestMissingContentTypeException(message);
            case 22:
                throw new RequestIncorrectContentTypeValueException(message);
            case 23:
                throw new RequestRateLimitException(message);
            case 24:
                throw new RequestInvalidJsonException(message);
            case 25:
                throw new RequestInvalidHttpMethodException(message);
            case 26:
                throw new RequestInvalidEndpointException(message);
            case 27:
                throw new RequestIncorrectHttpException(message);
            case 28:
                throw new RequestExceededLimitException(message);
            // Account Errors (30 - 39)
            case 30:
                throw new AccountMissingPaymentMethodException(message);
            case 31:
                throw new AccountPaymentRequiredException(message);
            case 32:
                throw new AccountBannedException(message);
            case 33:
                throw new AccountDisabledException(message);
            case 39:
                throw new InsertInvalidRangeException(message);
            // Column errors (40 - 59)
            case 40:
                throw new ColumnMissingParamException(message);
            case 41:
                throw new ColumnTypeException(message);
            case 42:
                throw new ColumnIntegerValuesException(message);
            case 43:
                throw new ColumnAlreadyExistsException(message);
            case 44:
                throw new ColumnLimitException(message);
            case 45:
                throw new ColumnTimeSeriesLimitException(message);
            case 46:
                throw new ColumnTimeSeriesSystemLimitException(message);
            case 47:
                throw new ColumnDecimalTypeException(message);
            case 48:
                throw new ColumnStorageValueException(message);
            case 49:
                throw new ColumnInvalidApiNameException(message);
            case 50:
                throw new ColumnInvalidNameException(message);
            case 51:
                throw new ColumnInvalidDescriptionException(message);
            case 52:
                throw new ColumnExceededDescriptionlengthException(message);
            case 53:
                throw new ColumnInvalidCardinalityException(message);
            case 54:
                throw new ColumnDecimalLimitException(message);
            case 55:
                throw new ColumnRangeLimitException(message);
            case 56:
                throw new ColumnExceededMaxNameLenghtException(message);
            case 57:
                throw new ColumnExceededMaxApiNameLenghtException(message);
            case 58:
                throw new ColumnEmptyEntityIdException(message);
            case 59:
                throw new ColumnExceededPermitedValueException(message);
            // Insert errors (60 - 79)
            case 60:
                throw new InsertInvalidDecimalPlacesException(message);
            case 61:
                throw new InsertEntityValueTypeException(message);
            case 62:
                throw new InsertColumnNameTypeException(message);
            case 63:
                throw new InsertColumnTypeException(message);
            case 64:
                throw new InsertEntityNameTooBigException(message);
            case 65:
                throw new InsertColumnValueTooBigException(message);
            case 66:
                throw new InsertTimeSeriesDateFormatException(message);
            case 67:
                throw new InsertColumnNotActiveException(message);
            case 68:
                throw new InsertIdLimitException(message);
            case 69:
                throw new InsertColumnLimitException(message);
            case 70:
                throw new InsertDateFormatException(message);
            case 71:
                throw new InsertColumnStringEmptyValueException(message);
            case 72:
                throw new InsertColumnTimeSeriesInvalidParameterException(message);
            case 73:
                throw new InsertColumnNumericInvalidValueException(message);
            case 74:
                throw new InsertColumnTimeSeriesMissingValueException(message);
            case 75:
                throw new QueryTimeSeriesInvalidPrecisionSecondsException(message);
            case 76:
                throw new QueryTimeSeriesInvalidPrecisionMinutesException(message);
            case 77:
                throw new QueryTimeSeriesInvalidPrecisionHoursException(message);
            case 78:
                throw new QueryDateFormatException(message);
            case 79:
                throw new QueryRelativeIntervalException(message);
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
                throw new QueryColumnNotActiveException(message);
            case 86:
                throw new QueryMissingOperatorException(message);
            case 87:
                throw new QueryIncompleteException(message);
            case 88:
                throw new QueryEventCountQueryException(message);
            case 89:
                throw new QueryInvalidMetricException(message);
            case 90:
                throw new QueryIntegerException(message);
            case 91:
                throw new QueryColumnLimitException(message);
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
                throw new QueryDataExtractionColumnLimitException(message);
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
            case 122:
                throw new QueryInvalidMinfreqException(message);
            case 123:
                throw new QueryExceededMaxNumberQuerysException(message);
            case 124:
                throw new QueryInvalidOperatorUsageException(message);
            case 125:
                throw new QueryInvalidParameterUsageException(message);
            case 126:
                throw new QueryParameterInvalidColumnUsageException(message);
            case 127:
                throw new QueryInvalidColumnUsageException(message);
            // Internal errors (130 - 140)
            case 130:
                throw new InternalException(message);
            case 131:
                throw new ColumnCreateInternalException(message);
        }
    }

    /**
     * Check if request was a successful
     *
     * @return true if the JSON result don't have errors
     */
    public boolean requestSuccessful() throws InternalException {
        final JSONObject data;

        try {
            data = new JSONObject(this.result);
        } catch (final JSONException exception) {
            throw new InternalException("SlicingDice: Internal error.");
        }

        if (data.has("errors")) {
            final JSONObject error = data.getJSONArray("errors").getJSONObject(0);
            this.raiseError(error);
        }

        return true;
    }
}
