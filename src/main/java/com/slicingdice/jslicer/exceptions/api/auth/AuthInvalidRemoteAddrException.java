package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AuthInvalidRemoteAddrException extends SlicingDiceException {
	public AuthInvalidRemoteAddrException(String message) {
		super(message);
	}
}
