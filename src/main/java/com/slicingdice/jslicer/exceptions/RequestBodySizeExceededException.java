package com.slicingdice.jslicer.exceptions;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import org.json.JSONObject;

public class RequestBodySizeExceededException extends SlicingDiceException {
    public RequestBodySizeExceededException(final JSONObject data) {
        super(data);
    }
}
