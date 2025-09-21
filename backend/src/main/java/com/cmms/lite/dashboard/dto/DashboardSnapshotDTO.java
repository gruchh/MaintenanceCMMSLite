package com.cmms.lite.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DashboardSnapshotDTO", description = "Migawka danych i statystyk na potrzeby dashboardu")
public class DashboardSnapshotDTO {

    @Schema(description = "Data i czas wygenerowania statystyk", example = "2025-09-20T10:30:00")
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Schema(description = "Statystyki dotyczące wydajności fabryki")
    private DashboardPerformanceInditatorDTO performance;

    @Schema(description = "Ogólne statystyki OEE")
    private DashboardOeeStatsOverallDTO oeeStatsOverall;

    @Schema(description = "Informacje o zalogowanym użytkowniku")
    private DashboardInfoAboutUser userInfo;

    @Schema(description = "Ranking pracowników wg liczby obsłużonych awarii")
    private DashboardRatingByBreakdownsDTO employeeBreakdownRanking;
}