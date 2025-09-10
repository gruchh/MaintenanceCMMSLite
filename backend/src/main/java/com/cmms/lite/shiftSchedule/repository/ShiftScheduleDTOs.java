package com.cmms.lite.shiftSchedule.repository;

import com.cmms.lite.shiftSchedule.entity.BrigadeType;
import com.cmms.lite.shiftSchedule.entity.ShiftType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public final class ShiftScheduleDTOs {

    private ShiftScheduleDTOs() {}

    @Schema(name = "ShiftScheduleGenerateRequest",
            description = "Dane potrzebne do wygenerowania harmonogramu zmian")
    public record GenerateRequest(
            @Schema(description = "Data rozpoczęcia harmonogramu (dzień 1 cyklu).")
            @NotNull @FutureOrPresent LocalDate startDate,

            @Schema(description = "Liczba dni do wygenerowania (domyślnie 28).", example = "28")
            @Positive Integer days
    ) {}

    @Schema(name = "ShiftEntryResponse", description = "Pojedynczy wpis w harmonogramie")
    public record EntryResponse(
            Long id,
            LocalDate date,
            BrigadeType brigade,
            ShiftType shift
    ) {}

    @Schema(name = "ShiftScheduleResponse", description = "Wygenerowany harmonogram z wpisami")
    public record ScheduleResponse(
            Long id,
            LocalDate startDate,
            LocalDate endDate,
            List<EntryResponse> entries
    ) {}
}
