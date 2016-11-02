package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QuerySavedNotExistsException extends SlicingDiceException {
	public QuerySavedNotExistsException(String message) {
		super(message);
	}
}
