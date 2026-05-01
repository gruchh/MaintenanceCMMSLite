package com.cmms.lite.shiftSchedule;

import com.cmms.lite.shiftSchedule.entity.BrigadeType;
import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import com.cmms.lite.shiftSchedule.entity.ShiftType;
import com.cmms.lite.shiftSchedule.factory.ShiftEntryFactory;
import com.cmms.lite.shiftSchedule.state.DayShiftState;
import com.cmms.lite.shiftSchedule.state.NightShiftState;
import com.cmms.lite.shiftSchedule.state.ShiftStateRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftEntryFactoryTest {

    @Mock
    ShiftStateRegistry stateRegistry;

    @InjectMocks
    ShiftEntryFactory factory;

    ShiftSchedule schedule;

    @BeforeEach
    void setUp() {
        schedule = ShiftSchedule.builder()
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 1, 28))
                .build();
    }

    @Test
    void create_returnsEntryWithCorrectSchedule() {
        when(stateRegistry.resolve(anyChar())).thenReturn(DayShiftState.INSTANCE);

        ShiftEntry entry = factory.create(schedule, LocalDate.of(2025, 1, 1), BrigadeType.A, 0);

        assertThat(entry.getSchedule()).isEqualTo(schedule);
    }

    @Test
    void create_returnsEntryWithCorrectBrigade() {
        when(stateRegistry.resolve(anyChar())).thenReturn(DayShiftState.INSTANCE);

        ShiftEntry entry = factory.create(schedule, LocalDate.of(2025, 1, 1), BrigadeType.B, 0);

        assertThat(entry.getBrigadeType()).isEqualTo(BrigadeType.B);
    }

    @Test
    void create_returnsEntryWithCorrectWorkDate() {
        when(stateRegistry.resolve(anyChar())).thenReturn(DayShiftState.INSTANCE);
        LocalDate date = LocalDate.of(2025, 3, 15);

        ShiftEntry entry = factory.create(schedule, date, BrigadeType.A, 0);

        assertThat(entry.getWorkDate()).isEqualTo(date);
    }

    @Test
    void create_returnsEntryWithShiftTypeMappedFromState() {
        when(stateRegistry.resolve(anyChar())).thenReturn(NightShiftState.INSTANCE);

        ShiftEntry entry = factory.create(schedule, LocalDate.of(2025, 1, 1), BrigadeType.C, 0);

        assertThat(entry.getShiftType()).isEqualTo(ShiftType.NIGHT);
    }
}