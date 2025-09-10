package com.cmms.lite.sparePart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "CreateSparePartDTO", description = "Data required to create a new spare part")
public record CreateSparePartDTO(
        @NotBlank(message = "Part name cannot be blank.")
        @Size(max = 150, message = "Part name cannot exceed 150 characters.")
        String name,

        @NotNull(message = "Part price cannot be null.")
        @DecimalMin(value = "0.01", message = "Part price must be greater than 0.")
        BigDecimal price,

        @Size(max = 150, message = "Producer name cannot exceed 150 characters.")
        String producer
) {}