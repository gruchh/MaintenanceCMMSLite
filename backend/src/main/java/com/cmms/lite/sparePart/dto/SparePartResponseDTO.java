package com.cmms.lite.sparePart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "A response object with spare part data")
public record SparePartResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        String producer
) {
}