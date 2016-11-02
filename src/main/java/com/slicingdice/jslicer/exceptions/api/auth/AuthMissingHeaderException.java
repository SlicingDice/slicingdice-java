package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AuthMissingHeaderException extends SlicingDiceException {
	public AuthMissingHeaderException(String message) {
		super(message);
	}
}
