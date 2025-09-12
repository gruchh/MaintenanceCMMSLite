package com.cmms.lite.shiftSchedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "ShiftScheduleResponseDTO", description = "Wygenerowany harmonogram z wpisami")
public record ShiftScheduleResponseDTO(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        List<ShiftEntryResponseDTO> entries
) {}