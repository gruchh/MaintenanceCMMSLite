package com.cmms.lite.breakdown.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class BreakdownNotFoundException extends ResourceNotFoundException {
    public BreakdownNotFoundException(String message) {
        super(message);
    }
}