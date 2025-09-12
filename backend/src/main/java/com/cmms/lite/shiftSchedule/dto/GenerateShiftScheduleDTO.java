package com.cmms.lite.shiftSchedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GenerateShiftScheduleDTO",
        description = "Dane potrzebne do wygenerowania harmonogramu zmian")
public class GenerateShiftScheduleDTO {

    @Schema(description = "Data rozpoczęcia harmonogramu (dzień 1 cyklu).")
    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @Schema(description = "Liczba dni do wygenerowania (domyślnie 28).", example = "28")
    @Positive
    private Integer days;
}