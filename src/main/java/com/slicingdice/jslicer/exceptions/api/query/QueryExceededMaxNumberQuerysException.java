package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryExceededMaxNumberQuerysException extends SlicingDiceException {
	public QueryExceededMaxNumberQuerysException(String message) {
		super(message);
	}
}
