package com.cmms.lite.shiftSchedule.dto;

import com.cmms.lite.shiftSchedule.entity.BrigadeType;
import com.cmms.lite.shiftSchedule.entity.ShiftType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "ShiftEntryResponse", description = "Pojedynczy wpis w harmonogramie")
public record ShiftEntryResponseDTO(
        Long id,
        LocalDate date,
        BrigadeType brigade,
        ShiftType shift
) {}