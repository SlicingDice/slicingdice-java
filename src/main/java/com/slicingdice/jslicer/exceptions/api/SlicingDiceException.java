package com.slicingdice.jslicer.exceptions.api;

import org.json.JSONException;
import org.json.JSONObject;

public class SlicingDiceException extends RuntimeException {

    private int code;
    private String message;
    private Object moreInfo;

    public SlicingDiceException(final String message) {
        super(message);
    }

    public SlicingDiceException(final JSONObject data) {
        super(data.getString("message"));

        try {
            this.code = data.getInt("code");
        } catch (final JSONException ignored) {
        }
        try {
            this.moreInfo = data.get("more-info");
        } catch (final JSONException ignored) {
        }
        this.message = data.getString("message");
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
