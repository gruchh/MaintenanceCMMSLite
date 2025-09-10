package com.cmms.lite.breakdownType.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BreakdownTypeResponseDTO", description = "Represents a breakdown type for the frontend")
public record BreakdownTypeResponseDTO(
        @Schema(description = "Enum value of the breakdown type", example = "MECHANICAL")
        String value,

        @Schema(description = "Display name for the breakdown type", example = "Mechanical Failure")
        String displayName
) {}