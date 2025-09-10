package com.cmms.lite.machine.controller;

import com.cmms.lite.machine.MachineDTOs;
import com.cmms.lite.machine.MachineUpdateRequest;
import com.cmms.lite.machine.dto.MachineDetailsResponse;
import com.cmms.lite.machine.service.MachineService;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/machines", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Machine", description = "API for managing machines and equipment")
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MachineDetailsResponse>> getAllMachines(Pageable pageable) {
        return ResponseEntity.ok(machineService.getAllMachines(pageable));
    }

    @GetMapping("/list")
    public ResponseEntity<List<MachineDetailsResponse>> getAllMachinesAsList() {
        return ResponseEntity.ok(machineService.getAllMachinesAsList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MachineDetailsResponse> getMachineById(@PathVariable Long id) {
        return ResponseEntity.ok(machineService.getMachineById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MachineDetailsResponse> createMachine(
            @Valid @RequestBody MachineDTOs.CreateRequest createRequest) {
        MachineDetailsResponse response = machineService.createMachine(createRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MachineDetailsResponse> updateMachine(
            @PathVariable Long id,
            @Valid @RequestBody MachineUpdateRequest updateRequest) {
        return ResponseEntity.ok(machineService.updateMachine(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id) {
        machineService.deleteMachine(id);
        return ResponseEntity.noContent().build();
    }
}