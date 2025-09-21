package com.cmms.lite.dashboard.controller;

import com.cmms.lite.dashboard.dto.*;
import com.cmms.lite.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API for dashboard stats")
public class DashboardController {

    private final DashboardService dashboardService;


    public ResponseEntity<DashboardSnapshotDTO> getAllStats() {
        List<DashboardPerformanceInditatorDTO> weeklyPerformance = dashboardService.getWeeklyPerformance();
        DashboardPerformanceInditatorDTO performanceDTO = new DashboardPerformanceInditatorDTO(weeklyPerformance);

        DashboardOeeStatsOverallDTO oeeStats = new DashboardOeeStatsOverallDTO();
        DashboardInfoAboutUser userInfo = new DashboardInfoAboutUser();
        DashboardRatingByBreakdownsDTO ranking = new DashboardRatingByBreakdownsDTO();

        DashboardSnapshotDTO snapshot = DashboardSnapshotDTO.builder()
                .performance(performanceDTO)
                .oeeStatsOverall(oeeStats)
                .userInfo(userInfo)
                .employeeBreakdownRanking(ranking)
                .build();

        return ResponseEntity.ok(snapshot);
    }
}