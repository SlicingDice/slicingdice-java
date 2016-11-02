package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestMissingContentTypeException extends SlicingDiceException {
	public RequestMissingContentTypeException(String message) {
		super(message);
	}
}
