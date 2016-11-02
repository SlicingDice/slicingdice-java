package com.slicingdice.jslicer.exceptions.api.account;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AccountDisabledException extends SlicingDiceException {
	public AccountDisabledException(String message) {
		super(message);
	}
}
