package com.cmms.lite.shiftSchedule.state;

import com.cmms.lite.shiftSchedule.entity.ShiftType;

public class OffShiftState implements ShiftState {
    public static final OffShiftState INSTANCE = new OffShiftState();
    private OffShiftState() {}

    @Override public ShiftType toShiftType()    { return ShiftType.OFF; }
    @Override public String    displaySymbol()  { return "W"; }
    @Override public boolean   isWorking()      { return false; }
}