package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryLevelLimitException extends SlicingDiceException {
	public QueryLevelLimitException(String message) {
		super(message);
	}
}
