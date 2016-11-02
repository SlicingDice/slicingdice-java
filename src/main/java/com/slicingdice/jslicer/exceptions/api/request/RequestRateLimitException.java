package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestRateLimitException extends SlicingDiceException {
	public RequestRateLimitException(String message) {
		super(message);
	}
}
