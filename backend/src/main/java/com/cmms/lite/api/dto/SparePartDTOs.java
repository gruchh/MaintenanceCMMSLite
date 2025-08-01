package com.cmms.lite.api.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public final class SparePartDTOs {

    private SparePartDTOs() {
    }

    public record CreateRequest(
            @NotBlank(message = "Part name cannot be blank.")
            @Size(max = 150)
            String name,

            @NotNull(message = "Part price cannot be null.")
            @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
            BigDecimal price,

            @Size(max = 150)
            String producer
    ) {}

    public record UpdateRequest(
            @NotBlank(message = "Part name cannot be blank.")
            @Size(max = 150)
            String name,

            @NotNull(message = "Part price cannot be null.")
            @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
            BigDecimal price,

            @Size(max = 150)
            String producer
    ) {}

    public record Response(
            Long id,
            String name,
            BigDecimal price,
            String producer
    ) {}
}