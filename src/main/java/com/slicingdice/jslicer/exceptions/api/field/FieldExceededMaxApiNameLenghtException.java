package com.slicingdice.jslicer.exceptions.api.field;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class FieldExceededMaxApiNameLenghtException extends SlicingDiceException {
	public FieldExceededMaxApiNameLenghtException(String message) {
		super(message);
	}
}
