package com.cmms.lite.employeeRole.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmployeeRoleResponseDTO", description = "A response object with employee role data")
public record EmployeeRoleResponseDTO(
        Long id,
        String name
) {}