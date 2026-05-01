package com.cmms.lite.shiftSchedule.state;

import com.cmms.lite.shiftSchedule.entity.ShiftType;

public class NightShiftState implements ShiftState {
    public static final NightShiftState INSTANCE = new NightShiftState();
    private NightShiftState() {}

    @Override public ShiftType toShiftType() { return ShiftType.NIGHT; }
    @Override public String displaySymbol() { return "N"; }
    @Override public boolean isWorking() { return true; }
}