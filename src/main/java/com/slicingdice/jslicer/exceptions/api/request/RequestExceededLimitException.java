package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestExceededLimitException extends SlicingDiceException {
	public RequestExceededLimitException(String message) {
		super(message);
	}
}
