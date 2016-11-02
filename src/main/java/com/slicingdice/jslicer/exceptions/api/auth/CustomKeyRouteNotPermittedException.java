package com.slicingdice.jslicer.exceptions.api.auth;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class CustomKeyRouteNotPermittedException extends SlicingDiceException {
	public CustomKeyRouteNotPermittedException(String message) {
		super(message);
	}
}
