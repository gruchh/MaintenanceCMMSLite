package com.cmms.lite.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateEmployeeDTO", description = "Dane wymagane do stworzenia nowego pracownika")
public class CreateEmployeeDTO {

    @Schema(description = "ID istniejącego użytkownika, który ma zostać pracownikiem.", example = "1")
    @NotNull(message = "User ID cannot be null.")
    private Long userId;

    @Schema(description = "Imię pracownika.", example = "Jan")
    @NotBlank(message = "Imię nie może być puste.")
    @Size(max = 50, message = "Imię nie może przekraczać 50 znaków.")
    private String firstName;

    @Schema(description = "Nazwisko pracownika.", example = "Kowalski")
    @NotBlank(message = "Nazwisko nie może być puste.")
    @Size(max = 50, message = "Nazwisko nie może przekraczać 50 znaków.")
    private String lastName;

    @Schema(description = "URL awatara pracownika.", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "ID roli pracownika.", example = "3")
    @NotNull(message = "Role ID cannot be null.")
    private Long roleId;
}