package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryMissingNameParamException extends SlicingDiceException {
	public QueryMissingNameParamException(String message) {
		super(message);
	}
}
