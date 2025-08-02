package com.cmms.lite.api.controller;

import com.cmms.lite.api.dto.SparePartDTOs;
import com.cmms.lite.service.SparePartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spare-parts")
@RequiredArgsConstructor
@Tag(name = "Spare Part API", description = "API for managing spare parts inventory")
public class SparePartController {

    private final SparePartService sparePartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find all spare parts", description = "Returns a paginated list of all available spare parts. Requires authentication.")
    public ResponseEntity<Page<SparePartDTOs.Response>> findAll(Pageable pageable) {
        return ResponseEntity.ok(sparePartService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find a spare part by ID", description = "Returns a single spare part by its ID. Requires authentication.")
    public ResponseEntity<SparePartDTOs.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sparePartService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new spare part", description = "Adds a new spare part to the inventory. Requires ADMIN role.")
    public ResponseEntity<SparePartDTOs.Response> create(@RequestBody SparePartDTOs.CreateRequest createRequest) {
        SparePartDTOs.Response response = sparePartService.save(createRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing spare part", description = "Updates an existing spare part by its ID. Requires ADMIN role.")
    public ResponseEntity<SparePartDTOs.Response> update(@PathVariable Long id, @RequestBody SparePartDTOs.UpdateRequest updateRequest) {
        return ResponseEntity.ok(sparePartService.update(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a spare part", description = "Deletes a spare part from the inventory by its ID. Requires ADMIN role.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sparePartService.delete(id);
        return ResponseEntity.noContent().build();
    }
}