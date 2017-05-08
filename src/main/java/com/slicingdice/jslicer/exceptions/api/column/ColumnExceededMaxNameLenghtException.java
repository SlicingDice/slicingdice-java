package com.slicingdice.jslicer.exceptions.api.column;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class ColumnExceededMaxNameLenghtException extends SlicingDiceException {
	public ColumnExceededMaxNameLenghtException(String message) {
		super(message);
	}
}
