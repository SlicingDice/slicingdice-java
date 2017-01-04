package com.slicingdice.jslicer.exceptions.api.request;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class RequestIncorrectHttpException extends SlicingDiceException {
	public RequestIncorrectHttpException(String message) {
		super(message);
	}
}
