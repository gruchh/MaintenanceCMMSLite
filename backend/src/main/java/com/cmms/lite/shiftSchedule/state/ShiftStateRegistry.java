package com.cmms.lite.shiftSchedule.state;

import com.cmms.lite.shiftSchedule.exception.ShiftNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ShiftStateRegistry {

    private static final Map<Character, ShiftState> STATES = Map.of(
            'D', DayShiftState.INSTANCE,
            'N', NightShiftState.INSTANCE,
            'W', OffShiftState.INSTANCE
    );

    public ShiftState resolve(char symbol) {
        ShiftState state = STATES.get(symbol);
        if (state == null) {
            throw new ShiftNotFoundException("ShiftState not found: " + symbol);
        }
        return state;
    }
}