package com.slicingdice.jslicer.exceptions;

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import org.json.JSONObject;

public class IndexEntitiesLimitException extends SlicingDiceException {
    public IndexEntitiesLimitException(final JSONObject data) {
        super(data);
    }
}
