package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestInvalidJsonException extends SlicingDiceException {
	public RequestInvalidJsonException(String message) {
		super(message);
	}
}
