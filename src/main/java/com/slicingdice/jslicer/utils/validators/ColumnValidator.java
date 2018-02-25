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

import com.slicingdice.jslicer.exceptions.client.InvalidColumnDescriptionException;
import com.slicingdice.jslicer.exceptions.client.InvalidColumnException;
import com.slicingdice.jslicer.exceptions.client.InvalidColumnNameException;
import com.slicingdice.jslicer.exceptions.client.InvalidColumnTypeException;

import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Slicing Dice validator to column JSONObject.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class ColumnValidator {

    private final Object data;
    private final List<String> validTypeColumns;

    public ColumnValidator(final Object data) {
        this.data = data;
        this.validTypeColumns = Arrays.asList(
                "unique-id", "boolean", "string", "integer", "decimal",
                "enumerated", "date", "integer-time-series",
                "decimal-time-series", "string-time-series", "datetime");
    }

    /**
     * Checks if column has a name and if name has a length less than 80 chars.
     */
    private void validateColumnName(final JSONObject json)
            throws InvalidColumnException, InvalidColumnNameException {
        if (!json.has("name")) {
            throw new InvalidColumnException("The column should have a name.");
        } else {
            final String name = json.getString("name");
            if (name.trim().length() == 0) {
                throw new InvalidColumnNameException("The column's name can't be empty/None.");
            } else if (name.length() > 80) {
                throw new InvalidColumnNameException(
                        "The column's name have a very big name.(Max: 80 chars)");
            }
        }
    }

    /**
     * Checks if column has a type valid.
     */
    private void validateColumnType(final JSONObject json)
            throws InvalidColumnException, InvalidColumnTypeException {
        if (!json.has("type")) {
            throw new InvalidColumnException("The column should have a type.");
        }
        final String typeColumn = json.getString("type");
        if (!this.validTypeColumns.contains(typeColumn)) {
            throw new InvalidColumnTypeException("This column have a invalid type.");
        }
    }

    /**
     * Checks if column has a description and if description has a length less than 300 chars.
     */
    private void validateColumnDescription(final JSONObject json)
            throws InvalidColumnDescriptionException {
        final String description = json.getString("description");
        if (description.trim().length() == 0) {
            throw new InvalidColumnDescriptionException(
                    "The column's description can't be empty/None.");
        } else if (description.length() > 300) {
            throw new InvalidColumnDescriptionException(
                    "The column's description have a very big content. (Max: 300 chars)");
        }
    }

    /**
     * Check the decimal type
     */
    private void validateColumnDecimalType(final JSONObject json) throws InvalidColumnException {
        final List<String> decimalTypes = Arrays.asList("decimal", "decimal-time-series");
        if (!decimalTypes.contains(json.getString("type"))) {
            throw new InvalidColumnException("The decimal type is not a valid one");
        }
    }

    /**
     * Checks if enumerated column is valid
     */
    private void validateEnumeratedType(final JSONObject json) throws InvalidColumnException {
        if (!json.has("range")) {
            throw new InvalidColumnException("The 'enumerate' type needs of the 'range' parameter.");
        }
    }

    /**
     * Checks if a column of type 'string' has the key 'cardinality'.
     */
    private void checkStringTypeIntegrity(final JSONObject json) throws InvalidColumnException {
        if (!json.has("cardinality")) {
            throw new InvalidColumnException(
                    "The column with type string should have 'cardinality' key.");
        }
        final List<String> cardinalityTypes = Arrays.asList("high", "low");

        if (!cardinalityTypes.contains(json.getString("cardinality"))) {
            throw new InvalidColumnException("The column 'cardinality' has invalid value.");
        }
    }

    /**
     * Validate the column
     *
     * @return true if column is valid and false otherwise
     */
    public boolean validator() {
        if (this.data instanceof JSONObject) {
            validateJsonObject((JSONObject) this.data);
        } else if (this.data instanceof JSONArray) {
            final JSONArray jsonArray = (JSONArray) this.data;

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject json = jsonArray.getJSONObject(i);
                validateJsonObject(json);
            }
        } else {
            return false;
        }

        return true;
    }

    private void validateJsonObject(final JSONObject jsonObject) {
        this.validateColumnName(jsonObject);
        this.validateColumnType(jsonObject);
        final String type = jsonObject.getString("type");
        if (type.equals("string")) {
            this.checkStringTypeIntegrity(jsonObject);
        }
        if (type.equals("enumerated")) {
            this.validateEnumeratedType(jsonObject);
        }
        if (jsonObject.has("description")) {
            this.validateColumnDescription(jsonObject);
        }
        if (jsonObject.has("decimal-place")) {
            this.validateColumnDecimalType(jsonObject);
        }
    }
}
