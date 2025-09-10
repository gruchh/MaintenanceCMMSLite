package com.cmms.lite.breakdown.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CloseBreakdownDTO", description = "Dane wymagane do zamknięcia zgłoszenia awarii")
public class CloseBreakdownDTO {

    @NotBlank(message = "Specialist comment cannot be blank.")
    private String specialistComment;
}