package com.cmms.lite.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UpdateEmployeeAddressDTO", description = "Dane do aktualizacji adresu. Wszystkie pola są opcjonalne.")
public class UpdateEmployeeAddressDTO {

    @Schema(description = "Ulica i numer", example = "Aleje Jerozolimskie 96")
    private String street;

    @Schema(description = "Miasto", example = "Warszawa")
    private String city;

    @Schema(description = "Kod pocztowy", example = "00-807")
    private String postalCode;

    @Schema(description = "Kraj", example = "Polska")
    private String country;
}