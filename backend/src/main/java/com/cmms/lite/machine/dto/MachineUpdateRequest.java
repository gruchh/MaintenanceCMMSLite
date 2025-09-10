package com.cmms.lite.machine;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MachineUpdateRequest", description = "Data required to update an existing machine")
public class MachineUpdateRequest {

    @NotBlank(message = "Machine code cannot be blank.")
    @Size(min = 2, max = 50)
    private String code;

    @NotBlank(message = "Machine full name cannot be blank.")
    @Size(min = 2, max = 200)
    private String fullName;

    @Size(max = 255)
    private String serialNumber;

    @Size(max = 100)
    private String manufacturer;

    @PastOrPresent
    private LocalDate productionDate;

    private String description;
}