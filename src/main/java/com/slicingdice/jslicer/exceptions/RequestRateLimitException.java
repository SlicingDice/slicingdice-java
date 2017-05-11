package com.slicingdice.jslicer.exceptions;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import org.json.JSONObject;

public class RequestRateLimitException extends SlicingDiceException {
    public RequestRateLimitException(final JSONObject data) {
        super(data);
    }
}
