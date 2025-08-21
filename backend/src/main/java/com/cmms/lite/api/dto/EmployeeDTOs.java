package com.cmms.lite.api.dto;

import com.cmms.lite.core.entity.EducationLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class EmployeeDTOs {

    private EmployeeDTOs() {}

    @Schema(name = "EmployeeCreateRequest", description = "Data required to create a new employee")
    public record CreateRequest(
            @Schema(description = "ID of the existing user to be linked as an employee.", example = "1")
            @NotNull
            Long userId,

            @Schema(description = "ID of the employee's role.", example = "3")
            @NotNull
            Long roleId
    ) {}

    @Schema(name = "EmployeeDetailsRequest", description = "Data for creating or updating employee's details")
    public record DetailsRequest(
            @NotBlank String phoneNumber,
            @NotNull @Past LocalDate dateOfBirth,
            @NotNull LocalDate hireDate,
            @NotBlank String street,
            @NotBlank String city,
            @NotBlank String postalCode,
            @NotBlank String country,
            LocalDate contractEndDate,
            @NotNull @Positive BigDecimal salary,
            @NotNull EducationLevel educationLevel,
            String fieldOfStudy,
            String emergencyContactName,
            String emergencyContactPhone
    ) {}

    @Schema(name = "EmployeeUpdateRequest", description = "Data required to update an employee's role and details")
    public record UpdateRequest(
            @Schema(description = "ID of the employee's new role.", example = "2")
            @NotNull(message = "Role ID cannot be null.")
            @Positive(message = "Role ID must be a positive number.")
            Long roleId,

            @Schema(description = "The detailed information for the employee.")
            @NotNull(message = "Employee details cannot be null.")
            @Valid
            DetailsRequest details
    ) {}


    @Schema(name = "EmployeeResponse", description = "A response object with detailed employee data")
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
    ) {}

    @Schema(name = "EmployeeSummaryResponse", description = "A response object with summary employee data")
    public record SummaryResponse(
            Long id,
            String username,
            String fullName,
            String avatarUrl,
            String role
    ) {}
}