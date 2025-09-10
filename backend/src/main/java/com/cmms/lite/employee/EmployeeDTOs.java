package com.cmms.lite.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class EmployeeDTOs {

    private EmployeeDTOs() {
    }

    @Schema(name = "EmployeeCreateRequest", description = "Dane wymagane do stworzenia nowego pracownika")
    public record CreateRequest(
            @Schema(description = "ID istniejącego użytkownika, który ma zostać pracownikiem.", example = "1")
            @NotNull
            Long userId,

            @Schema(description = "Imię pracownika.", example = "Jan")
            @NotBlank(message = "Imię nie może być puste.")
            @Size(max = 50, message = "Imię nie może przekraczać 50 znaków.")
            String firstName,

            @Schema(description = "Nazwisko pracownika.", example = "Kowalski")
            @NotBlank(message = "Nazwisko nie może być puste.")
            @Size(max = 50, message = "Nazwisko nie może przekraczać 50 znaków.")
            String lastName,

            @Schema(description = "URL awatara pracownika.", example = "https://example.com/avatar.jpg")
            String avatarUrl,

            @Schema(description = "ID roli pracownika.", example = "3")
            @NotNull
            Long roleId
    ) {
    }

    @Schema(name = "AddressUpdateRequest", description = "Dane do aktualizacji adresu. Wszystkie pola są opcjonalne.")
    public record AddressUpdateRequest(
            @Schema(description = "Ulica i numer", example = "Aleje Jerozolimskie 96")
            String street,

            @Schema(description = "Miasto", example = "Warszawa")
            String city,

            @Schema(description = "Kod pocztowy", example = "00-807")
            String postalCode,

            @Schema(description = "Kraj", example = "Polska")
            String country
    ) {
    }

    @Schema(name = "EmployeeUpdateRequest", description = "Dane do aktualizacji pracownika. Wszystkie pola są opcjonalne.")
    public record UpdateRequest(
            @Schema(description = "Imię pracownika.", example = "Jan")
            @Size(max = 50, message = "Imię nie może przekraczać 50 znaków.")
            String firstName,

            @Schema(description = "Nazwisko pracownika.", example = "Kowalski")
            @Size(max = 50, message = "Nazwisko nie może przekraczać 50 znaków.")
            String lastName,

            @Schema(description = "URL awatara pracownika.", example = "https://example.com/avatar.jpg")
            String avatarUrl,

            @Schema(description = "ID nowej roli pracownika.", example = "2")
            @Positive(message = "ID roli musi być liczbą dodatnią.")
            Long roleId,

            @Schema(description = "Numer telefonu.", example = "500 100 200")
            String phoneNumber,

            @Schema(description = "Data urodzenia.", example = "1990-05-15")
            @Past(message = "Data urodzenia musi być w przeszłości.")
            LocalDate dateOfBirth,

            @Schema(description = "Data zatrudnienia.", example = "2022-01-10")
            LocalDate hireDate,

            @Schema(description = "Szczegóły adresu.")
            @Valid
            AddressUpdateRequest address,

            @Schema(description = "Data końca kontraktu (opcjonalnie).", example = "2026-12-31")
            LocalDate contractEndDate,

            @Schema(description = "Wynagrodzenie.", example = "7500.50")
            @Positive(message = "Wynagrodzenie musi być liczbą dodatnią.")
            BigDecimal salary,

            @Schema(description = "Poziom wykształcenia.", example = "MASTER")
            EducationLevel educationLevel,

            @Schema(description = "Kierunek studiów.", example = "Informatyka")
            String fieldOfStudy,

            @Schema(description = "Imię i nazwisko kontaktu alarmowego.", example = "Anna Kowalska")
            String emergencyContactName,

            @Schema(description = "Numer telefonu kontaktu alarmowego.", example = "501 202 303")
            String emergencyContactPhone
    ) {
    }

    @Schema(name = "EmployeeResponse", description = "Szczegółowe dane pracownika")
    public record Response(
            Long id,
            String username,
            String lastName,
            String firstName,
            String fullName,
            String email,
            String avatarUrl,
            String role,
            String phoneNumber,
            LocalDate dateOfBirth,
            LocalDate hireDate,
            String street,
            String city,
            String postalCode,
            String country,
            LocalDate contractEndDate,
            BigDecimal salary,
            EducationLevel educationLevel,
            String fieldOfStudy,
            String emergencyContactName,
            String emergencyContactPhone,
            int age,
            LocalDate retirementDate
    ) {
    }

    @Schema(name = "EmployeeSummaryResponse", description = "Podstawowe dane pracownika")
    public record SummaryResponse(Long id, String username, String fullName, String avatarUrl, String role) {
    }
}