package com.cmms.lite.breakdown.controller;

import com.cmms.lite.breakdown.dto.*;
import com.cmms.lite.breakdown.service.BreakdownService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/breakdowns", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Breakdown", description = "API for managing breakdowns")
public class BreakdownController {

    private final BreakdownService breakdownService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BreakdownResponseDTO>> getAllBreakdowns(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(breakdownService.searchBreakdowns(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BreakdownResponseDTO> getBreakdownById(@PathVariable Long id) {
        return ResponseEntity.ok(breakdownService.getBreakdownById(id));
    }

    @PostMapping("/report")
    public ResponseEntity<BreakdownResponseDTO> reportBreakdown(@Valid @RequestBody CreateBreakdownDTO request) {
        BreakdownResponseDTO response = breakdownService.createBreakdown(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{breakdownId}/parts")
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    public ResponseEntity<BreakdownResponseDTO> addPartToBreakdown(@PathVariable Long breakdownId, @Valid @RequestBody AddPartBreakdownDTO request) {
        return ResponseEntity.ok(breakdownService.addPartToBreakdown(breakdownId, request));
    }

    @DeleteMapping("/{breakdownId}/parts/{usedPartId}")
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    public ResponseEntity<BreakdownResponseDTO> removePartFromBreakdown(@PathVariable Long breakdownId, @PathVariable Long usedPartId) {
        return ResponseEntity.ok(breakdownService.removePartFromBreakdown(breakdownId, usedPartId));
    }

    @PatchMapping("/{breakdownId}/close")
    @PreAuthorize("hasRole('TECHNICAN')")
    public ResponseEntity<BreakdownResponseDTO> closeBreakdown(@PathVariable Long breakdownId, @Valid @RequestBody CloseBreakdownDTO request) {
        return ResponseEntity.ok(breakdownService.closeBreakdown(breakdownId, request));
    }

    @GetMapping("/performance/latest")
    public ResponseEntity<BreakdownResponseDTO> getLatestBreakdown() {
        return ResponseEntity.ok(breakdownService.getLatestBreakdown());
    }

    @GetMapping("/performance/stats")
    public ResponseEntity<BreakdownStatsDTO> getBreakdownStats() {
        return ResponseEntity.ok(breakdownService.getBreakdownStats());
    }
}