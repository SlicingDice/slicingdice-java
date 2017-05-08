package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryColumnLimitException extends SlicingDiceException {
	public QueryColumnLimitException(String message) {
		super(message);
	}
}
