package com.cmms.lite.sparePart.controller;

import com.cmms.lite.sparePart.dto.CreateSparePartDTO;
import com.cmms.lite.sparePart.dto.SparePartResponseDTO;
import com.cmms.lite.sparePart.dto.UpdateSparePartDTO;
import com.cmms.lite.sparePart.service.SparePartService;
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
@RequestMapping(value = "/api/spare-parts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Spare Part", description = "API for managing spare parts inventory")
public class SparePartController {

    private final SparePartService sparePartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SparePartResponseDTO>> getAllSpareParts(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(sparePartService.getAllSpareParts(search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SparePartResponseDTO> getSparePartById(@PathVariable Long id) {
        return ResponseEntity.ok(sparePartService.getSparePartById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SparePartResponseDTO> createSparePart(@Valid @RequestBody CreateSparePartDTO request) {
        SparePartResponseDTO response = sparePartService.createSparePart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SparePartResponseDTO> updateSparePart(@PathVariable Long id, @Valid @RequestBody UpdateSparePartDTO request) {
        return ResponseEntity.ok(sparePartService.updateSparePart(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSparePart(@PathVariable Long id) {
        sparePartService.deleteSparePart(id);
        return ResponseEntity.noContent().build();
    }
}