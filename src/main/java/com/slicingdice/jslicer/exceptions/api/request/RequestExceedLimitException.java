package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestExceedLimitException extends SlicingDiceException {
	public RequestExceedLimitException(String message) {
		super(message);
	}
}
