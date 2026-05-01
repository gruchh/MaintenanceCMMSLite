package com.cmms.lite.shiftSchedule.factory;

import com.cmms.lite.shiftSchedule.entity.BrigadeType;
import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import com.cmms.lite.shiftSchedule.state.ShiftState;
import com.cmms.lite.shiftSchedule.state.ShiftStateRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShiftEntryFactory {

    private final ShiftStateRegistry stateRegistry;

    private static final String[] BASE_PATTERN = {
            "DDWWDDD",
            "WWDDWWW",
            "NNWWNNN",
            "WWNNWWW"
    };

    private static final Map<BrigadeType, String[]> BRIGADE_PATTERNS = Map.of(
            BrigadeType.A, new String[]{BASE_PATTERN[0], BASE_PATTERN[1], BASE_PATTERN[2], BASE_PATTERN[3]},
            BrigadeType.B, new String[]{BASE_PATTERN[1], BASE_PATTERN[2], BASE_PATTERN[3], BASE_PATTERN[0]},
            BrigadeType.C, new String[]{BASE_PATTERN[2], BASE_PATTERN[3], BASE_PATTERN[0], BASE_PATTERN[1]},
            BrigadeType.D, new String[]{BASE_PATTERN[3], BASE_PATTERN[0], BASE_PATTERN[1], BASE_PATTERN[2]}
    );

    public ShiftEntry create(ShiftSchedule schedule, LocalDate date,
                             BrigadeType brigade, int dayIndex) {

        int week = (dayIndex / 7) % 4;
        int day = dayIndex % 7;

        char symbol = BRIGADE_PATTERNS.get(brigade)[week].charAt(day);
        ShiftState state = stateRegistry.resolve(symbol);

        return ShiftEntry.builder()
                .schedule(schedule)
                .workDate(date)
                .brigadeType(brigade)
                .shiftType(state.toShiftType())
                .build();
    }
}