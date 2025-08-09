package com.cmms.lite.api.controller;

import com.cmms.lite.api.dto.BreakdownTypeDTOs;
import com.cmms.lite.service.BreakdownTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/breakdown-types", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Breakdown Types", description = "API for providing defined breakdown types")
public class BreakdownTypeController {

    private final BreakdownTypeService breakdownTypeService;

    @GetMapping
    public ResponseEntity<List<BreakdownTypeDTOs.Response>> getBreakdownTypes() {
        List<BreakdownTypeDTOs.Response> types = breakdownTypeService.getAllBreakdownTypesAsDto();
        return ResponseEntity.ok(types);
    }
}