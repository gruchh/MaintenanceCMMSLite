package com.cmms.lite.employee.dto;

import com.cmms.lite.employee.entity.Brigade;
import com.cmms.lite.employee.entity.EducationLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;

@Schema(name = "EmployeeResponseDTO", description = "Szczegółowe dane pracownika")
public record EmployeeResponseDTO(
        @Schema(description = "Unique identifier of the employee", example = "1")
        Long id,

        @Schema(description = "Username of the employee", example = "jkowalski")
        String username,

        @Schema(description = "Last name of the employee", example = "Kowalski")
        String lastName,

        @Schema(description = "First name of the employee", example = "Jan")
        String firstName,

        @Schema(description = "Full name of the employee", example = "Jan Kowalski")
        String fullName,

        @Schema(description = "Email address", example = "jan.kowalski@company.com")
        String email,

        @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        @Schema(description = "Employee role", example = "Technician")
        String role,

        @Schema(description = "Phone number", example = "500 100 200")
        String phoneNumber,

        @Schema(description = "Date of birth", example = "1990-05-15")
        LocalDate dateOfBirth,

        @Schema(description = "Hire date", example = "2022-01-10")
        LocalDate hireDate,

        @Schema(description = "Street address", example = "Aleje Jerozolimskie 96")
        String street,

        @Schema(description = "City", example = "Warszawa")
        String city,

        @Schema(description = "Postal code", example = "00-807")
        String postalCode,

        @Schema(description = "Country", example = "Polska")
        String country,

        @Schema(description = "Contract end date", example = "2026-12-31")
        LocalDate contractEndDate,

        @Schema(description = "Salary", example = "7500.50")
        BigDecimal salary,

        @Schema(description = "Education level", example = "MASTER")
        EducationLevel educationLevel,

        @Schema(description = "Brigade", example = "A")
        Brigade brigade,

        @Schema(description = "Field of study", example = "Informatyka")
        String fieldOfStudy,

        @Schema(description = "Emergency contact name", example = "Anna Kowalska")
        String emergencyContactName,

        @Schema(description = "Emergency contact phone", example = "501 202 303")
        String emergencyContactPhone,

        @Schema(description = "Age in years", example = "33")
        int age,

        @Schema(description = "Calculated retirement date", example = "2055-05-15")
        LocalDate retirementDate
) {}