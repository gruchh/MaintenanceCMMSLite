package com.cmms.lite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class MachineDTOs {

    private MachineDTOs() {}

    @Schema(name = "MachineCreateRequest", description = "Data required to create a new machine")
    public record CreateRequest(
            @NotBlank(message = "Machine code cannot be blank.") @Size(min = 2, max = 50) String code,
            @NotBlank(message = "Machine full name cannot be blank.") @Size(min = 2, max = 200) String fullName,
            @Size(max = 255) String serialNumber,
            @Size(max = 100) String manufacturer,
            @PastOrPresent LocalDate productionDate,
            String description
    ) {}

    @Schema(name = "MachineUpdateRequest", description = "Data required to update an existing machine")
    public record UpdateRequest(
            @NotBlank(message = "Machine code cannot be blank.") @Size(min = 2, max = 50) String code,
            @NotBlank(message = "Machine full name cannot be blank.") @Size(min = 2, max = 200) String fullName,
            @Size(max = 255) String serialNumber,
            @Size(max = 100) String manufacturer,
            @PastOrPresent LocalDate productionDate,
            String description
    ) {}

    @Schema(name = "MachineDetailsResponse", description = "A detailed response object with machine data")
    public record Response(
            Long id,
            String code,
            String fullName,
            String serialNumber,
            String manufacturer,
            LocalDate productionDate,
            String description
    ) {}

    @Schema(name = "MachineSummaryResponse", description = "Basic machine information (for lists and nested objects)")
    public record SummaryResponse(
            Long id,
            String code,
            String fullName
    ) {}
}