package com.cmms.lite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public final class BreakdownTypeDTOs {

    private BreakdownTypeDTOs() {}

    @Schema(name = "BreakdownTypeResponse", description = "Represents a breakdown type for the frontend")
    public record Response(
            @NotBlank String value,
            @NotBlank String displayName
    ) {}
}