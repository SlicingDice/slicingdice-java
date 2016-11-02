package com.slicingdice.jslicer.exceptions.api.account;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AccountPaymentRequiredException extends SlicingDiceException {
	public AccountPaymentRequiredException(String message) {
		super(message);
	}
}
