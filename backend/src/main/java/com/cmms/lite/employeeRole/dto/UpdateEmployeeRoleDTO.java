package com.cmms.lite.employeeRole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UpdateEmployeeRoleDTO", description = "Data required to update an employee role")
public class UpdateEmployeeRoleDTO {

    @NotBlank(message = "Role name cannot be blank.")
    @Size(max = 50, message = "Role name cannot be longer than 50 characters.")
    private String name;
}