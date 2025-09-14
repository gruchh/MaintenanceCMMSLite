package com.cmms.lite.breakdown.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "BreakdownPerformanceIndicatorDTO", description = "Statystyki związane z wydajnością w kontekście awarii")
public record BreakdownPerformanceIndicatorDTO(

        @Schema(description = "Data, której dotyczą statystyki wydajności", example = "2025-09-13")
        LocalDate date,

        @Schema(description = "Wartość wskaźnika wydajności (np. w procentach)", example = "98.5")
        double performance
) {}