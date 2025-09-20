package com.cmms.lite.dashboard.controller;

import com.cmms.lite.breakdown.dto.BreakdownResponseDTO;
import com.cmms.lite.dashboard.dto.DashboardDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API for dashboard stats")
public class DashboardController {

    @GetMapping
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardDTO> getAllStats() {
        return ResponseEntity.ok(new DashboardDTO());
    }
}
