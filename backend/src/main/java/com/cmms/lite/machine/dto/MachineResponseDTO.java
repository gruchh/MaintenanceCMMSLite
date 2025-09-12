package com.cmms.lite.machine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "MachineResponseDTO", description = "A detailed response object with machine data")
public record MachineResponseDTO(
        Long id,
        String code,
        String fullName,
        String serialNumber,
        String manufacturer,
        LocalDate productionDate,
        String description
) {}