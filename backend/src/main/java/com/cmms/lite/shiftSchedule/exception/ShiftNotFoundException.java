package com.cmms.lite.shiftSchedule.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class ShiftNotFoundException extends ResourceNotFoundException {
    public ShiftNotFoundException(String message) {
        super(message);
    }
}