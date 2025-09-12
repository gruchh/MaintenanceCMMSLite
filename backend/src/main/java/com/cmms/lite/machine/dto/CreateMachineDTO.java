package com.cmms.lite.machine.dto;

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
@Schema(name = "CreateMachineDTO", description = "Data required to create a new machine")
public class CreateMachineDTO {

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