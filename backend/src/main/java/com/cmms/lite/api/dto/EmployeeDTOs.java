package com.cmms.lite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public final class EmployeeDTOs {

    private EmployeeDTOs() {}

    @Schema(name = "EmployeeUpdateRequest", description = "Data required to update an employee's role")
    public record UpdateRequest(
            @NotNull(message = "Role ID cannot be null.")
            @Positive(message = "Role ID must be a positive number.")
            Long roleId
    ) {}

    @Schema(name = "EmployeeResponse", description = "A response object with detailed employee data")
    public record Response(
            Long id,
            String username,
            String email,
            String role
    ) {}

    @Schema(name = "EmployeeSummaryResponse", description = "A response object with summary employee data")
    public record SummaryResponse(
            Long id,
            String username
    ) {}
}