package com.cmms.lite.breakdown.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "UsedPartBreakdownDTO", description = "Obiekt odpowiedzi dla części użytej podczas awarii")
public record UsedPartBreakdownDTO(
        @Schema(description = "Unique identifier of the used part record", example = "1")
        Long id,

        @Schema(description = "ID of the spare part", example = "105")
        Long sparePartId,

        @Schema(description = "Name of the spare part", example = "Oil Filter")
        String sparePartName,

        @Schema(description = "Quantity used", example = "2")
        Integer quantity,

        @Schema(description = "Price per unit at the time of use", example = "29.99")
        BigDecimal pricePerUnit
) {}