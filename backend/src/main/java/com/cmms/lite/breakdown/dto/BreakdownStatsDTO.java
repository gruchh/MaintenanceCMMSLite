package com.cmms.lite.breakdown.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BreakdownStatsDTO", description = "Statystyki związane z awariami")
public record BreakdownStatsDTO(
        @Schema(description = "Number of days since the last breakdown", example = "5")
        Long daysSinceLastBreakdown,

        @Schema(description = "Number of breakdowns in the last week", example = "3")
        Long breakdownsLastWeek,

        @Schema(description = "Number of breakdowns in the last month", example = "12")
        Long breakdownsLastMonth,

        @Schema(description = "Number of breakdowns in the current year", example = "89")
        Long breakdownsCurrentYear,

        @Schema(description = "Average breakdown duration in minutes", example = "145.5")
        Double averageBreakdownDurationMinutes
) {}