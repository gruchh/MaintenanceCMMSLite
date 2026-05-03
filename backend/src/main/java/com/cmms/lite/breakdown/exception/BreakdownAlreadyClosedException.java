package com.cmms.lite.breakdown.exception;

import com.cmms.lite.exception.IllegalOperationException;

public class BreakdownAlreadyClosedException extends IllegalOperationException {
    public BreakdownAlreadyClosedException(String message) {
        super(message);
    }
}