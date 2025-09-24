package com.cmms.lite.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "DashboardRatingByBreakdownsDTO", description = "Statystyki związane z uczestnictwem w awariach przez poszczególnych pracowników")
public record DashboardRatingByBreakdownsDTO(
        @Schema(
                description = "List of workers with their breakdown counts and details",
                example = "[{\"id\": 1, \"fullName\": \"Robert Malinowski\", \"email\": \"robert.malinowski@company.com\", \"avatarUrl\": \"https://example.com/avatar1.jpg\", \"role\": \"Technik\", \"breakdownCount\": 21}, {\"id\": 2, \"fullName\": \"Ewa Nowak\", \"email\": \"ewa.nowak@company.com\", \"avatarUrl\": \"https://example.com/avatar2.jpg\", \"role\": \"Inżynier Procesu\", \"breakdownCount\": 18}]"
        )
        List<WorkerBreakdownDTO> workers,

        @Schema(description = "Associated area", example = "Linia Montażowa A")
        String associatedArea
) {
    @Schema(name = "WorkerBreakdownDTO", description = "Szczegółowe dane pracownika wraz z liczbą awarii")
    public record WorkerBreakdownDTO(
            @Schema(description = "Unique identifier of the employee", example = "1")
            Long id,

            @Schema(description = "Full name of the employee", example = "Robert Malinowski")
            String fullName,

            @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
            String avatarUrl,

            @Schema(description = "Employee role", example = "Technician")
            String role,

            @Schema(description = "Total number of breakdowns assigned to this worker", example = "21")
            Integer breakdownCount
    ) {}
}