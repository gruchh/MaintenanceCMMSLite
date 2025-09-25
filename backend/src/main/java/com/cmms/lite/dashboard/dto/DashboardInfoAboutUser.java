package com.cmms.lite.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "DashboardInfoAboutUser", description = "Podstawowe informacje o pracowniku, w tym liczba awarii i data emerytury.")
public record DashboardInfoAboutUser(
        @Schema(description = "First name of the employee", example = "Jan")
        String firstName,

        @Schema(description = "Last name of the employee", example = "Kowalski")
        String lastName,

        @Schema(description = "Total number of breakdowns assigned to the employee", example = "15")
        Long breakdownCount,

        @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        @Schema(description = "Calculated retirement date", example = "2055-05-15")
        LocalDate retirementDate
) {}