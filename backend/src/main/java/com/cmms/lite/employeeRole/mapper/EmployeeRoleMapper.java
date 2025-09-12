package com.cmms.lite.employeeRole.mapper;

import com.cmms.lite.employeeRole.dto.CreateEmployeeRoleDTO;
import com.cmms.lite.employeeRole.dto.EmployeeRoleResponseDTO;
import com.cmms.lite.employeeRole.dto.UpdateEmployeeRoleDTO;
import com.cmms.lite.employeeRole.entity.EmployeeRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmployeeRoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    EmployeeRole toEntity(CreateEmployeeRoleDTO request);

    EmployeeRoleResponseDTO toResponse(EmployeeRole role);

    List<EmployeeRoleResponseDTO> toResponseList(List<EmployeeRole> roles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromRequest(UpdateEmployeeRoleDTO request, @MappingTarget EmployeeRole role);
}