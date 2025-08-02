package com.cmms.lite.api.controller;

import com.cmms.lite.api.dto.BreakdownDTOs;
import com.cmms.lite.service.BreakdownService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/breakdowns")
@RequiredArgsConstructor
@Tag(name = "Breakdown API", description = "API for managing breakdowns")
public class BreakdownController {

    private final BreakdownService breakdownService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find all breakdowns", description = "Returns a paginated list of all breakdowns. Requires authentication.")
    public ResponseEntity<Page<BreakdownDTOs.Response>> findAll(Pageable pageable) {
        return ResponseEntity.ok(breakdownService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find a breakdown by ID", description = "Returns a single breakdown by its ID. Requires authentication.")
    public ResponseEntity<BreakdownDTOs.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(breakdownService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    @Operation(summary = "Create a new breakdown", description = "Creates a new breakdown entry. Requires SUBCONTRACTOR role or higher.")
    public ResponseEntity<BreakdownDTOs.Response> createBreakdown(@RequestBody BreakdownDTOs.CreateRequest request) {
        BreakdownDTOs.Response response = breakdownService.createBreakdown(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{breakdownId}/parts")
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    @Operation(summary = "Add a part to a breakdown", description = "Adds a used part to a specific breakdown. Requires SUBCONTRACTOR role or higher.")
    public ResponseEntity<BreakdownDTOs.Response> addPartToBreakdown(@PathVariable Long breakdownId, @RequestBody BreakdownDTOs.AddPartRequest request) {
        return ResponseEntity.ok(breakdownService.addPartToBreakdown(breakdownId, request));
    }

    @DeleteMapping("/{breakdownId}/parts/{usedPartId}")
    @PreAuthorize("hasRole('SUBCONTRACTOR')")
    @Operation(summary = "Remove a part from a breakdown", description = "Removes a used part from a specific breakdown. Requires SUBCONTRACTOR role or higher.")
    public ResponseEntity<BreakdownDTOs.Response> removePartFromBreakdown(@PathVariable Long breakdownId, @PathVariable Long usedPartId) {
        return ResponseEntity.ok(breakdownService.removePartFromBreakdown(breakdownId, usedPartId));
    }

    @PatchMapping("/{breakdownId}/close")
    @PreAuthorize("hasRole('TECHNICAN')")
    @Operation(summary = "Close a breakdown", description = "Marks a breakdown as closed. Requires TECHNICAN role or higher.")
    public ResponseEntity<BreakdownDTOs.Response> closeBreakdown(@PathVariable Long breakdownId, @RequestBody BreakdownDTOs.CloseRequest request) {
        return ResponseEntity.ok(breakdownService.closeBreakdown(breakdownId, request));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get breakdown statistics", description = "Returns public statistics about breakdowns. No authentication required.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    public ResponseEntity<BreakdownDTOs.BreakdownStatsDTO> getStats() {
        return ResponseEntity.ok(breakdownService.getBreakdownStats());
    }
}