package com.cmms.lite.machine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "MachineDetailsResponse", description = "A detailed response object with machine data")
public record MachineDetailsResponse(
        Long id,
        String code,
        String fullName,
        String serialNumber,
        String manufacturer,
        LocalDate productionDate,
        String description
) {}