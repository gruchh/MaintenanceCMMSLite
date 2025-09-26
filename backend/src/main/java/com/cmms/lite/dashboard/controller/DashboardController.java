package com.cmms.lite.dashboard.controller;

import com.cmms.lite.dashboard.dto.DashboardFactoryStatsDTO;
import com.cmms.lite.dashboard.dto.DashboardSnapshotDTO;
import com.cmms.lite.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API for dashboard stats")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardSnapshotDTO> getDashboardSnapshot() {
        DashboardSnapshotDTO snapshot = dashboardService.getDashboardSnapshot();
        return ResponseEntity.ok(snapshot);
    }

    @GetMapping("/factory-stats")
    public ResponseEntity<DashboardFactoryStatsDTO> getFactoryStats() {
        DashboardFactoryStatsDTO factoryStats = dashboardService.getDashboardFactoryStats();
        return ResponseEntity.ok(factoryStats);
    }
}