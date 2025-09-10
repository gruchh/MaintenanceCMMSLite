package com.cmms.lite.breakdown.dto;

import com.cmms.lite.breakdownType.entity.BreakdownType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateBreakdownDTO", description = "Dane wymagane do utworzenia nowego zgłoszenia awarii")
public class CreateBreakdownDTO {

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    @NotNull(message = "Machine ID cannot be null.")
    private Long machineId;

    @NotNull(message = "Breakdown type cannot be null.")
    private BreakdownType type;
}