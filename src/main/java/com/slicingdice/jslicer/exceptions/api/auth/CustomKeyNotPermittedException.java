package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class CustomKeyNotPermittedException extends SlicingDiceException {
	public CustomKeyNotPermittedException(String message) {
		super(message);
	}
}
