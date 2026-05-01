package com.cmms.lite.shiftSchedule;

import com.cmms.lite.shiftSchedule.exception.ShiftNotFoundException;
import com.cmms.lite.shiftSchedule.state.DayShiftState;
import com.cmms.lite.shiftSchedule.state.NightShiftState;
import com.cmms.lite.shiftSchedule.state.OffShiftState;
import com.cmms.lite.shiftSchedule.state.ShiftStateRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShiftStateRegistryTest {

    private ShiftStateRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ShiftStateRegistry();
    }

    @Test
    void resolve_D_returnsDayShiftState() {
        assertThat(registry.resolve('D')).isInstanceOf(DayShiftState.class);
    }

    @Test
    void resolve_N_returnsNightShiftState() {
        assertThat(registry.resolve('N')).isInstanceOf(NightShiftState.class);
    }

    @Test
    void resolve_W_returnsOffShiftState() {
        assertThat(registry.resolve('W')).isInstanceOf(OffShiftState.class);
    }

    @Test
    void resolve_unknownSymbol_throwsShiftNotFoundException() {
        assertThatThrownBy(() -> registry.resolve('X'))
                .isInstanceOf(ShiftNotFoundException.class)
                .hasMessageContaining("X");
    }
}