package com.cmms.lite.shiftSchedule.state;

import com.cmms.lite.shiftSchedule.entity.ShiftType;

public class DayShiftState implements ShiftState {
    public static final DayShiftState INSTANCE = new DayShiftState();
    private DayShiftState() {}

    @Override public ShiftType toShiftType()    { return ShiftType.DAY; }
    @Override public String    displaySymbol()  { return "D"; }
    @Override public boolean   isWorking()      { return true; }
}