package com.cmms.lite.shiftSchedule.state;

import com.cmms.lite.shiftSchedule.entity.ShiftType;

public interface ShiftState {
    ShiftType toShiftType();
    String displaySymbol();
    boolean isWorking();
}