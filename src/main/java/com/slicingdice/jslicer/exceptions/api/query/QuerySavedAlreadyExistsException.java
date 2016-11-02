package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QuerySavedAlreadyExistsException extends SlicingDiceException {
	public QuerySavedAlreadyExistsException(String message) {
		super(message);
	}
}
