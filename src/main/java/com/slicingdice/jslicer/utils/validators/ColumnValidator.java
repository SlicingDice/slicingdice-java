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
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Slicing Dice validator to column JSONObject.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class ColumnValidator {

    private final JSONObject data;
    private final List<String> validTypeColumns;

    public ColumnValidator(final JSONObject data) {
        this.data = data;
        this.validTypeColumns = Arrays.asList(
                "unique-id", "boolean", "string", "integer", "decimal",
                "enumerated", "date", "integer-time-series",
                "decimal-time-series", "string-time-series");
    }


    /**
     * Checks if column has a name and if name has a length less than 80 chars.
     */
    private void validateColumnName() throws InvalidColumnException, InvalidColumnNameException {
        if (!this.data.has("name")) {
            throw new InvalidColumnException("The column should have a name.");
        } else {
            final String name = this.data.getString("name");
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
    private void validateColumnType() throws InvalidColumnException, InvalidColumnTypeException {
        if (!this.data.has("type")) {
            throw new InvalidColumnException("The column should have a type.");
        }
        final String typeColumn = this.data.getString("type");
        if (!this.validTypeColumns.contains(typeColumn)) {
            throw new InvalidColumnTypeException("This column have a invalid type.");
        }
    }

    /**
     * Checks if column has a description and if description has a length less than 300 chars.
     */
    private void validateColumnDescription() throws InvalidColumnDescriptionException {
        final String description = this.data.getString("description");
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
     * @throws InvalidColumnException
     */
    private void validateColumnDecimalType() throws InvalidColumnException {
        final List<String> decimalTypes = Arrays.asList("decimal", "decimal-time-series");
        if (!decimalTypes.contains(this.data.getString("type"))) {
            throw new InvalidColumnException("The decimal type is not a valid one");
        }
    }

    /**
     * Checks if enumerated column is valid
     */
    private void validateEnumeratedType() throws InvalidColumnException {
        if (!this.data.has("range")) {
            throw new InvalidColumnException("The 'enumerate' type needs of the 'range' parameter.");
        }
    }

    /**
     * Checks if a column of type 'string' has the key 'cardinality'.
     */
    private void checkStringTypeIntegrity() throws InvalidColumnException {
        if (!this.data.has("cardinality")) {
            throw new InvalidColumnException(
                    "The column with type string should have 'cardinality' key.");
        }
        final List<String> cardinalityTypes = Arrays.asList("high", "low");

        if (!cardinalityTypes.contains(this.data.getString("cardinality"))) {
            throw new InvalidColumnException("The column 'cardinality' has invalid value.");
        }
    }

    /**
     * Validate the column
     *
     * @return true if column is valid and false otherwise
     */
    public boolean validator() {
        this.validateColumnName();
        this.validateColumnType();
        final String type = this.data.getString("type");
        if (type.equals("string")) {
            this.checkStringTypeIntegrity();
        }
        if (type.equals("enumerated")) {
            this.validateEnumeratedType();
        }
        if (this.data.has("description")) {
            this.validateColumnDescription();
        }
        if (this.data.has("decimal-place")) {
            this.validateColumnDecimalType();
        }
        return true;
    }
}
