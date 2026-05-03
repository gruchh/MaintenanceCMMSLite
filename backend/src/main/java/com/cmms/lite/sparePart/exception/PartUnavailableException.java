package com.cmms.lite.sparePart.exception;

import com.cmms.lite.exception.IllegalOperationException;

public class PartUnavailableException extends IllegalOperationException {
    public PartUnavailableException(String message) {
        super(message);
    }
}