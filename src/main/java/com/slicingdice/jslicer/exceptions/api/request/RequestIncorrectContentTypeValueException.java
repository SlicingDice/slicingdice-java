package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestIncorrectContentTypeValueException extends SlicingDiceException {
	public RequestIncorrectContentTypeValueException(String message) {
		super(message);
	}
}
