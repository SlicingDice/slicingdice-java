package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestInvalidHttpMethodException extends SlicingDiceException {
	public RequestInvalidHttpMethodException(String message) {
		super(message);
	}
}
