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

import com.slicingdice.jslicer.exceptions.client.InvalidFieldDescriptionException;
import com.slicingdice.jslicer.exceptions.client.InvalidFieldException;
import com.slicingdice.jslicer.exceptions.client.InvalidFieldNameException;
import com.slicingdice.jslicer.exceptions.client.InvalidFieldTypeException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Slicing Dice validator to field JSONObject.
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class FieldValidator {

    private final JSONObject data;
    private final List<String> validTypeFields;

    public FieldValidator(final JSONObject data) {
        this.data = data;
        this.validTypeFields = Arrays.asList(
                "unique-id", "boolean", "string", "integer", "decimal",
                "enumerated", "date", "integer-time-series",
                "decimal-time-series", "string-time-series");
    }


    /**
     * Checks if field has a name and if name has a length less than 80 chars.
     */
    private void validateFieldName() throws InvalidFieldException, InvalidFieldNameException {
        if (!this.data.has("name")) {
            throw new InvalidFieldException("The field should have a name.");
        } else {
            final String name = this.data.getString("name");
            if (name.trim().length() == 0) {
                throw new InvalidFieldNameException("The field's name can't be empty/None.");
            } else if (name.length() > 80) {
                throw new InvalidFieldNameException(
                        "The field's name have a very big name.(Max: 80 chars)");
            }
        }
    }

    /**
     * Checks if field has a type valid.
     */
    private void validateFieldType() throws InvalidFieldException, InvalidFieldTypeException {
        if (!this.data.has("type")) {
            throw new InvalidFieldException("The field should have a type.");
        }
        final String typeField = this.data.getString("type");
        if (!this.validTypeFields.contains(typeField)) {
            throw new InvalidFieldTypeException("This field have a invalid type.");
        }
    }

    /**
     * Checks if field has a description and if description has a length less than 300 chars.
     */
    private void validateFieldDescription() throws InvalidFieldDescriptionException {
        final String description = this.data.getString("description");
        if (description.trim().length() == 0) {
            throw new InvalidFieldDescriptionException(
                    "The field's description can't be empty/None.");
        } else if (description.length() > 300) {
            throw new InvalidFieldDescriptionException(
                    "The field's description have a very big content. (Max: 300 chars)");
        }
    }

    /**
     * Check the decimal type
     * @throws InvalidFieldException
     */
    private void validateFieldDecimalType() throws InvalidFieldException {
        final List<String> decimalTypes = Arrays.asList("decimal", "decimal-time-series");
        if (!decimalTypes.contains(this.data.getString("type"))) {
            throw new InvalidFieldException("The decimal type is not a valid one");
        }
    }

    /**
     * Checks if enumerated field is valid
     */
    private void validateEnumeratedType() throws InvalidFieldException {
        if (!this.data.has("range")) {
            throw new InvalidFieldException("The 'enumerate' type needs of the 'range' parameter.");
        }
    }

    /**
     * Checks if a field of type 'string' has the key 'cardinality'.
     */
    private void checkStringTypeIntegrity() throws InvalidFieldException {
        if (!this.data.has("cardinality")) {
            throw new InvalidFieldException(
                    "The field with type string should have 'cardinality' key.");
        }
    }

    /**
     * Validate the field
     *
     * @return true if field is valid and false otherwise
     */
    public boolean validator() {
        this.validateFieldName();
        this.validateFieldType();
        final String type = this.data.getString("type");
        if (type.equals("string")) {
            this.checkStringTypeIntegrity();
        }
        if (type.equals("enumerated")) {
            this.validateEnumeratedType();
        }
        if (this.data.has("description")) {
            this.validateFieldDescription();
        }
        if (this.data.has("decimal-place")) {
            this.validateFieldDecimalType();
        }
        return true;
    }
}
