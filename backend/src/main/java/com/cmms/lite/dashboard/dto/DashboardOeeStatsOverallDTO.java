package com.cmms.lite.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DashboardOeeStatsOverallDTO(
        @Schema(description = "Overall Equipment Effectiveness percentage", example = "85.5")
        Double oeePercentage,

        @Schema(description = "Mean Time Between Failures (MTBF) in hours (year)", example = "250")
        Double mtbfYear,

        @Schema(description = "Mean Time Between Failures (MTBF) in hours (month)", example = "22")
        Double mtbfMonth,

        @Schema(description = "Mean Time To Repair (MTTR) in hours (year)", example = "1.5")
        Double mttrYear,

        @Schema(description = "Mean Time To Repair (MTTR) in hours (month)", example = "0.9")
        Double mttrMonth
) {}