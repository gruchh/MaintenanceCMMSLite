package com.cmms.lite.shiftSchedule.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class ShiftScheduleNotFoundException extends ResourceNotFoundException {
    public ShiftScheduleNotFoundException(String message) {
        super(message);
    }
}