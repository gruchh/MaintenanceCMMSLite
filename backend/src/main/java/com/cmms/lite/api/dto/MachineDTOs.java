package com.cmms.lite.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public final class MachineDTOs {

    private MachineDTOs() {
    }

    public record CreateRequest(
            @NotBlank(message = "Machine code cannot be blank.")
            @Size(min = 2, max = 50)
            String code,

            @NotBlank(message = "Machine full name cannot be blank.")
            @Size(min = 2, max = 200)
            String fullName,

            @Size(max = 255)
            String serialNumber,

            @Size(max = 100)
            String manufacturer,

            @PastOrPresent
            LocalDate productionDate,

            String description
    ) {}

    public record UpdateRequest(
            @NotBlank(message = "Machine code cannot be blank.")
            @Size(min = 2, max = 50)
            String code,

            @NotBlank(message = "Machine full name cannot be blank.")
            @Size(min = 2, max = 200)
            String fullName,

            @Size(max = 255)
            String serialNumber,

            @Size(max = 100)
            String manufacturer,

            @PastOrPresent
            LocalDate productionDate,

            String description
    ) {}

    public record Response(
            Long id,
            String code,
            String fullName,
            String serialNumber,
            String manufacturer,
            LocalDate productionDate,
            String description
    ) {}

    public record SummaryResponse(
            Long id,
            String code,
            String fullName
    ) {}
}