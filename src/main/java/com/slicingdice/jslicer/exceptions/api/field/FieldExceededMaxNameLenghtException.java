package com.slicingdice.jslicer.exceptions.api.field;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class FieldExceededMaxNameLenghtException extends SlicingDiceException {
	public FieldExceededMaxNameLenghtException(String message) {
		super(message);
	}
}
