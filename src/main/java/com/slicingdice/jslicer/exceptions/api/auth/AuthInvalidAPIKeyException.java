package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AuthInvalidAPIKeyException extends SlicingDiceException {
	public AuthInvalidAPIKeyException(String message) {
		super(message);
	}
}
