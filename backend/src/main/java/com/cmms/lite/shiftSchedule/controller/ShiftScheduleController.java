package com.cmms.lite.shiftSchedule.controller;

import com.cmms.lite.shiftSchedule.dto.GenerateShiftScheduleDTO;
import com.cmms.lite.shiftSchedule.dto.ShiftScheduleResponseDTO;
import com.cmms.lite.shiftSchedule.service.ShiftScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "ShiftSchedule", description = "API do zarządzania harmonogramami zmian")
public class ShiftScheduleController {

    private final ShiftScheduleService service;

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShiftScheduleResponseDTO> createShiftSchedule(
            @Valid @RequestBody GenerateShiftScheduleDTO request) {
        ShiftScheduleResponseDTO response = service.createShiftSchedule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShiftScheduleResponseDTO> getShiftScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getShiftScheduleById(id));
    }
}