package com.slicingdice.jslicer.exceptions;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import org.json.JSONObject;

public class DemoUnavailableException extends SlicingDiceException {
    public DemoUnavailableException(final JSONObject data) {
        super(data);
    }
}
