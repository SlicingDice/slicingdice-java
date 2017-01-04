package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestInvalidEndpointException extends SlicingDiceException {
	public RequestInvalidEndpointException(String message) {
		super(message);
	}
}
