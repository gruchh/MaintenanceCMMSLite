package com.cmms.lite.shiftSchedule;

import com.cmms.lite.shiftSchedule.entity.ShiftType;
import com.cmms.lite.shiftSchedule.state.DayShiftState;
import com.cmms.lite.shiftSchedule.state.NightShiftState;
import com.cmms.lite.shiftSchedule.state.OffShiftState;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ShiftStateTest {

    @Test
    void dayShift_isWorking() {
        assertThat(DayShiftState.INSTANCE.isWorking()).isTrue();
    }

    @Test
    void dayShift_hasCorrectSymbol() {
        assertThat(DayShiftState.INSTANCE.displaySymbol()).isEqualTo("D");
    }

    @Test
    void dayShift_mapsToCorrectShiftType() {
        assertThat(DayShiftState.INSTANCE.toShiftType()).isEqualTo(ShiftType.DAY);
    }

    @Test
    void nightShift_isWorking() {
        assertThat(NightShiftState.INSTANCE.isWorking()).isTrue();
    }

    @Test
    void nightShift_hasCorrectSymbol() {
        assertThat(NightShiftState.INSTANCE.displaySymbol()).isEqualTo("N");
    }

    @Test
    void nightShift_mapsToCorrectShiftType() {
        assertThat(NightShiftState.INSTANCE.toShiftType()).isEqualTo(ShiftType.NIGHT);
    }

    @Test
    void offShift_isNotWorking() {
        assertThat(OffShiftState.INSTANCE.isWorking()).isFalse();
    }

    @Test
    void offShift_hasCorrectSymbol() {
        assertThat(OffShiftState.INSTANCE.displaySymbol()).isEqualTo("W");
    }

    @Test
    void offShift_mapsToCorrectShiftType() {
        assertThat(OffShiftState.INSTANCE.toShiftType()).isEqualTo(ShiftType.OFF);
    }
}