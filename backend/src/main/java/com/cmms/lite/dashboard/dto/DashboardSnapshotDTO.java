package com.cmms.lite.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "DashboardSnapshotDTO", description = "Migawka danych i statystyk na potrzeby dashboardu")
public record DashboardSnapshotDTO(
        @Schema(description = "Data i czas wygenerowania statystyk", example = "2025-09-20T10:30:00")
        LocalDateTime generatedAt,

        @Schema(description = "Statystyki dotyczące wydajności fabryki z ostatnich 7 dni")
        List<DashboardPerformanceInditatorDTO> weeklyPerformance,

        @Schema(description = "Ogólne statystyki OEE")
        DashboardOeeStatsOverallDTO oeeStatsOverall,

        @Schema(description = "Informacje o zalogowanym użytkowniku")
        DashboardInfoAboutUser userInfo,

        @Schema(description = "Ranking pracowników wg liczby obsłużonych awarii")
        List<DashboardWorkerBreakdownDTO> workerBreakdownRanking
) {
    public DashboardSnapshotDTO(
            List<DashboardPerformanceInditatorDTO> weeklyPerformance,
            DashboardOeeStatsOverallDTO oeeStatsOverall,
            DashboardInfoAboutUser userInfo,
            List<DashboardWorkerBreakdownDTO> workerBreakdownRanking
    ) {
        this(LocalDateTime.now(), weeklyPerformance, oeeStatsOverall, userInfo, workerBreakdownRanking);
    }
}