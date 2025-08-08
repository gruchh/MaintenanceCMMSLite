package com.cmms.lite.api.controller;

import com.cmms.lite.api.dto.SparePartDTOs;
import com.cmms.lite.service.SparePartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Spare Part", description = "API for managing spare parts inventory")
public class    SparePartController {

    private final SparePartService sparePartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SparePartDTOs.Response>> getAllSpareParts(Pageable pageable) {
        return ResponseEntity.ok(sparePartService.getAllSpareParts(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SparePartDTOs.Response> getSparePartById(@PathVariable Long id) {
        return ResponseEntity.ok(sparePartService.getSparePartById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SparePartDTOs.Response> createSparePart(@Valid @RequestBody SparePartDTOs.CreateRequest request) {
        SparePartDTOs.Response response = sparePartService.createSparePart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SparePartDTOs.Response> updateSparePart(@PathVariable Long id, @Valid @RequestBody SparePartDTOs.UpdateRequest request) {
        return ResponseEntity.ok(sparePartService.updateSparePart(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSparePart(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return ResponseEntity.noContent().build();
    }
}