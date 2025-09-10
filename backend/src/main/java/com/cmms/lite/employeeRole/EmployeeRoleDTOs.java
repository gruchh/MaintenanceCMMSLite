package com.cmms.lite.employeeRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class EmployeeRoleDTOs {

    private EmployeeRoleDTOs() {}

    @Schema(name = "EmployeeRoleCreateRequest", description = "Data required to create a new employee role")
    public record CreateRequest(
            @NotBlank(message = "Role name cannot be blank.")
            @Size(max = 50, message = "Role name cannot be longer than 50 characters.")
            String name
    ) {}

    @Schema(name = "EmployeeRoleUpdateRequest", description = "Data required to update an employee role")
    public record UpdateRequest(
            @NotBlank(message = "Role name cannot be blank.")
            @Size(max = 50, message = "Role name cannot be longer than 50 characters.")
            String name
    ) {}

    @Schema(name = "EmployeeRoleResponse", description = "A response object with employee role data")
    public record Response(
            Long id,
            String name
    ) {}
}