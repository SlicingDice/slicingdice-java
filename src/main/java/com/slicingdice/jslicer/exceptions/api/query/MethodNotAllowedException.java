package com.slicingdice.jslicer.exceptions.api.query;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class MethodNotAllowedException extends SlicingDiceException {
	public MethodNotAllowedException(String message) {
		super(message);
	}
}
