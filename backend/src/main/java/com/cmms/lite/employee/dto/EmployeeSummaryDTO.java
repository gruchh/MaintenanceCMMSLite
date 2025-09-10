package com.cmms.lite.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmployeeSummaryDTO", description = "Podstawowe dane pracownika")
public record EmployeeSummaryDTO(
        @Schema(description = "Unique identifier of the employee", example = "1")
        Long id,

        @Schema(description = "Username of the employee", example = "jkowalski")
        String username,

        @Schema(description = "Full name of the employee", example = "Jan Kowalski")
        String fullName,

        @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        @Schema(description = "Employee role", example = "Technician")
        String role
) {}