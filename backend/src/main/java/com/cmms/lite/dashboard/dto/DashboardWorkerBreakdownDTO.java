package com.cmms.lite.dashboard.dto;

import com.cmms.lite.employee.entity.Brigade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DashboardWorkerBreakdownDTO", description = "Szczegółowe dane pracownika wraz z liczbą awarii")
    public record DashboardWorkerBreakdownDTO(
            @Schema(description = "Unique identifier of the employee", example = "1")
            Long id,

            @Schema(description = "First name of the employee", example = "Jan")
            String firstName,

            @Schema(description = "Last name of the employee", example = "Kowalski")
            String lastName,

            @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
            String avatarUrl,

            @Schema(description = "Employee role", example = "Technician")
            String role,

            @Schema(description = "Brigade", example = "A")
            Brigade brigade,

            @Schema(description = "Total number of breakdowns assigned to this worker", example = "21")
            Integer breakdownCount
    ) {}
