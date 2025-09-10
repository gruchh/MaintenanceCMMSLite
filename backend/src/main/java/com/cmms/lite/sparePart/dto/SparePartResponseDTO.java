package com.cmms.lite.sparePart;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class SparePartResponseDTO {
    @Schema(name = "SparePartResponse", description = "A response object with spare part data")
    record Response(
            Long id,
            String name,
            BigDecimal price,
            String producer
    ) {
    }
}