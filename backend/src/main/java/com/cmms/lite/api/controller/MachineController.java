package com.cmms.lite.api.controller;

import com.cmms.lite.api.dto.MachineDTOs;
import com.cmms.lite.service.MachineService;
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
@RequestMapping("/api/machines")
@RequiredArgsConstructor
@Tag(name = "Machine API", description = "API for managing machines and equipment")
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find all machines", description = "Returns a paginated list of all machines. Requires authentication.")
    public ResponseEntity<Page<MachineDTOs.Response>> findAll(Pageable pageable) {
        return ResponseEntity.ok(machineService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find a machine by ID", description = "Returns a single machine by its ID. Requires authentication.")
    public ResponseEntity<MachineDTOs.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(machineService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new machine", description = "Creates a new machine entry. Requires ADMIN role.")
    public ResponseEntity<MachineDTOs.Response> create(@RequestBody MachineDTOs.CreateRequest createRequest) {
        MachineDTOs.Response response = machineService.save(createRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing machine", description = "Updates an existing machine by its ID. Requires ADMIN role.")
    public ResponseEntity<MachineDTOs.Response> update(@PathVariable Long id, @RequestBody MachineDTOs.UpdateRequest updateRequest) {
        return ResponseEntity.ok(machineService.update(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a machine", description = "Deletes a machine by its ID. Requires ADMIN role.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        machineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}