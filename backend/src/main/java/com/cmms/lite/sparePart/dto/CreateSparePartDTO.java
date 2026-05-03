package com.cmms.lite.sparePart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateSparePartDTO", description = "Data required to create a new spare part")
public class CreateSparePartDTO {

    @NotBlank(message = "Part name cannot be blank.")
    @Size(max = 150, message = "Part name cannot exceed 150 characters.")
    private String name;

    @NotNull(message = "Part price cannot be null.")
    @DecimalMin(value = "0.01", message = "Part price must be greater than 0.")
    private BigDecimal price;

    @Size(max = 150, message = "Producer name cannot exceed 150 characters.")
    private String producer;

    @NotNull
    @Min(0)
    private Integer stockQuantity;
}