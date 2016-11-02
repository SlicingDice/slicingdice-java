package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class QueryExistsMissingIdsException extends SlicingDiceException {
	public QueryExistsMissingIdsException(String message) {
		super(message);
	}
}
