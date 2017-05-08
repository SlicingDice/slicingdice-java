package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryParameterInvalidColumnUsageException extends SlicingDiceException {
	public QueryParameterInvalidColumnUsageException(String message) {
		super(message);
	}
}
