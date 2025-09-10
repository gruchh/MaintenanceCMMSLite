package com.cmms.lite.sparePart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "UpdateSparePartDTO", description = "Data required to update a spare part (all fields are optional for PATCH operations)")
public record UpdateSparePartDTO(
        @Size(max = 150, message = "Part name cannot exceed 150 characters.")
        String name,

        @DecimalMin(value = "0.01", message = "Part price must be greater than 0.")
        BigDecimal price,

        @Size(max = 150, message = "Producer name cannot exceed 150 characters.")
        String producer
) {}