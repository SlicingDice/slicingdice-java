package com.slicingdice.jslicer.exceptions.api.field;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class FieldAlreadyExistsException extends SlicingDiceException {
	public FieldAlreadyExistsException(String message) {
		super(message);
	}
}
