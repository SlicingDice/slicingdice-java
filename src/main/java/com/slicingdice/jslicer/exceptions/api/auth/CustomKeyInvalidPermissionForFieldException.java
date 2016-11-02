package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class CustomKeyInvalidPermissionForFieldException extends SlicingDiceException {
	public CustomKeyInvalidPermissionForFieldException(String message) {
		super(message);
	}
}
