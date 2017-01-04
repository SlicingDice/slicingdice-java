package com.slicingdice.jslicer.exceptions.api.field;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class FieldExceededPermitedValueException extends SlicingDiceException {
	public FieldExceededPermitedValueException(String message) {
		super(message);
	}
}
