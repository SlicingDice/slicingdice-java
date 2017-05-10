package com.slicingdice.jslicer.exceptions;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import org.json.JSONObject;

public class IndexColumnsLimitException extends SlicingDiceException {
    public IndexColumnsLimitException(final JSONObject data) {
        super(data);
    }
}
