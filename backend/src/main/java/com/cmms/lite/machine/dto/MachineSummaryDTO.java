package com.cmms.lite.machine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MachineSummaryDTO", description = "Basic machine information (for lists and nested objects)")
public record MachineSummaryDTO(
        Long id,
        String code,
        String fullName
) {}