package com.cmms.lite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
public final class SparePartDTOs {

    private SparePartDTOs() {}

    @Schema(name = "SparePartCreateRequest", description = "Data required to create a new spare part")
    public record CreateRequest(
            @NotBlank(message = "Part name cannot be blank.") @Size(max = 150) String name,
            @NotNull(message = "Part price cannot be null.") @DecimalMin(value = "0.01") BigDecimal price,
            @Size(max = 150) String producer
    ) {}

    @Schema(name = "SparePartUpdateRequest", description = "Data required to update a spare part")
    public record UpdateRequest(
            @NotBlank(message = "Part name cannot be blank.") @Size(max = 150) String name,
            @NotNull(message = "Part price cannot be null.") @DecimalMin(value = "0.01") BigDecimal price,
            @Size(max = 150) String producer
    ) {}

    @Schema(name = "SparePartResponse", description = "A response object with spare part data")
    public record Response(
            Long id,
            String name,
            BigDecimal price,
            String producer
    ) {}
}