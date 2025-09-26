package com.cmms.lite.employee.dto;

import com.cmms.lite.employee.entity.Brigade;
import com.cmms.lite.employee.entity.EducationLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UpdateEmployeeDTO", description = "Dane do aktualizacji pracownika. Wszystkie pola są opcjonalne.")
public class UpdateEmployeeDTO {

    @Schema(description = "Imię pracownika.", example = "Jan")
    @Size(max = 50, message = "Imię nie może przekraczać 50 znaków.")
    private String firstName;

    @Schema(description = "Nazwisko pracownika.", example = "Kowalski")
    @Size(max = 50, message = "Nazwisko nie może przekraczać 50 znaków.")
    private String lastName;

    @Schema(description = "URL awatara pracownika.", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "ID nowej roli pracownika.", example = "2")
    @Positive(message = "ID roli musi być liczbą dodatnią.")
    private Long roleId;

    @Schema(description = "Numer telefonu.", example = "500 100 200")
    private String phoneNumber;

    @Schema(description = "Data urodzenia.", example = "1990-05-15")
    @Past(message = "Data urodzenia musi być w przeszłości.")
    private LocalDate dateOfBirth;

    @Schema(description = "Data zatrudnienia.", example = "2022-01-10")
    private LocalDate hireDate;

    @Schema(description = "Szczegóły adresu.")
    @Valid
    private UpdateEmployeeAddressDTO address;

    @Schema(description = "Data końca kontraktu (opcjonalnie).", example = "2026-12-31")
    private LocalDate contractEndDate;

    @Schema(description = "Wynagrodzenie.", example = "7500.50")
    @Positive(message = "Wynagrodzenie musi być liczbą dodatnią.")
    private BigDecimal salary;

    @Schema(description = "Poziom wykształcenia.", example = "MASTER")
    private EducationLevel educationLevel;

    @Schema(description = "Poziom wykształcenia.", example = "MASTER")
    private Brigade brigade;

    @Schema(description = "Kierunek studiów.", example = "Informatyka")
    private String fieldOfStudy;

    @Schema(description = "Imię i nazwisko kontaktu alarmowego.", example = "Anna Kowalska")
    private String emergencyContactName;

    @Schema(description = "Numer telefonu kontaktu alarmowego.", example = "501 202 303")
    private String emergencyContactPhone;
}