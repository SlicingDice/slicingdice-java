package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class CustomKeyInvalidOperationException extends SlicingDiceException {
	public CustomKeyInvalidOperationException(String message) {
		super(message);
	}
}
