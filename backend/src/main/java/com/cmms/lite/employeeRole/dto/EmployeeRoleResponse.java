package com.cmms.lite.employeeRole.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmployeeRoleResponse", description = "A response object with employee role data")
public record EmployeeRoleResponse(
        Long id,
        String name
) {}