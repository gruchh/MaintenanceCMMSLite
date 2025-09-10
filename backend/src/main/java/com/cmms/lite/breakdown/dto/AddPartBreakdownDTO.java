package com.cmms.lite.breakdown.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AddPartBreakdownDTO", description = "Dane wymagane do dodania części zamiennej do awarii")
public class AddPartBreakdownDTO {

    @Schema(description = "ID części zamiennej z magazynu.", example = "105")
    @NotNull(message = "Spare part ID cannot be null.")
    @Positive(message = "Spare part ID must be positive.")
    private Long sparePartId;

    @Schema(description = "Liczba użytych części.", example = "2")
    @NotNull(message = "Quantity cannot be null.")
    @Positive(message = "Quantity must be positive.")
    private Integer quantity;
}