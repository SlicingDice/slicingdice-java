package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryMissingQueryException extends SlicingDiceException {
	public QueryMissingQueryException(String message) {
		super(message);
	}
}
