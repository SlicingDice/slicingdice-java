package com.slicingdice.jslicer.exceptions.api;

import org.json.JSONObject;

public class SlicingDiceException extends RuntimeException {

	private int code;
	private String message;
	private Object moreInfo;

	public SlicingDiceException(final String message){
		super(message);
	}

	public SlicingDiceException(final JSONObject data){
		super(data.getString("message"));

		this.code = data.getInt("code");
		this.message = data.getString("message");
		this.moreInfo = data.get("more-info");
	}

	@Override
	public String toString() {
		return "SlicingDiceException{" +
				"code=" + this.code +
				", message='" + this.message + '\'' +
				", moreInfo=" + this.moreInfo +
				'}';
	}
}
