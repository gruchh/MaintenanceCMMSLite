package com.cmms.lite.api.controller;

import com.cmms.lite.api.dto.BreakdownDTOs;
import com.cmms.lite.service.BreakdownService;
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
    public ResponseEntity<Page<BreakdownDTOs.Response>> getAllBreakdowns(Pageable pageable) {
        return ResponseEntity.ok(breakdownService.getAllBreakdowns(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BreakdownDTOs.Response> getBreakdownById(@PathVariable Long id) {
        return ResponseEntity.ok(breakdownService.getBreakdownById(id));
    }

    @PostMapping("/report")
    public ResponseEntity<BreakdownDTOs.Response> reportBreakdown(@Valid @RequestBody BreakdownDTOs.CreateRequest request) {
        BreakdownDTOs.Response response = breakdownService.createBreakdown(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{breakdownId}/parts")
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    public ResponseEntity<BreakdownDTOs.Response> addPartToBreakdown(@PathVariable Long breakdownId, @Valid @RequestBody BreakdownDTOs.AddPartRequest request) {
        return ResponseEntity.ok(breakdownService.addPartToBreakdown(breakdownId, request));
    }

    @DeleteMapping("/{breakdownId}/parts/{usedPartId}")
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    public ResponseEntity<BreakdownDTOs.Response> removePartFromBreakdown(@PathVariable Long breakdownId, @PathVariable Long usedPartId) {
        return ResponseEntity.ok(breakdownService.removePartFromBreakdown(breakdownId, usedPartId));
    }

    @PatchMapping("/{breakdownId}/close")
    @PreAuthorize("hasRole('TECHNICAN')")
    public ResponseEntity<BreakdownDTOs.Response> closeBreakdown(@PathVariable Long breakdownId, @Valid @RequestBody BreakdownDTOs.CloseRequest request) {
        return ResponseEntity.ok(breakdownService.closeBreakdown(breakdownId, request));
    }

    @GetMapping("/latest")
    public ResponseEntity<BreakdownDTOs.Response> getLatestBreakdown() {
        return ResponseEntity.ok(breakdownService.getLatestBreakdown());
    }

    @GetMapping("/stats")
    public ResponseEntity<BreakdownDTOs.BreakdownStatsDTO> getBreakdownStats() {
        return ResponseEntity.ok(breakdownService.getBreakdownStats());
    }
}