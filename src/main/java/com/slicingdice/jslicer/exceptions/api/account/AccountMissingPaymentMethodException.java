package com.slicingdice.jslicer.exceptions.api.account;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;

public class AccountMissingPaymentMethodException extends SlicingDiceException {
	public AccountMissingPaymentMethodException(String message) {
		super(message);
	}
}
