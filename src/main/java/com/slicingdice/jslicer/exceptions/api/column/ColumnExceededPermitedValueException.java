package com.slicingdice.jslicer.exceptions.api.column;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class ColumnExceededPermitedValueException extends SlicingDiceException {
	public ColumnExceededPermitedValueException(String message) {
		super(message);
	}
}
