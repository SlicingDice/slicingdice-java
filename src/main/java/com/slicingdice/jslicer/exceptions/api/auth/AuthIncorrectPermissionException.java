package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AuthIncorrectPermissionException extends SlicingDiceException {
	public AuthIncorrectPermissionException(String message) {
		super(message);
	}
}
