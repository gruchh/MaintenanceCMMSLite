package com.cmms.lite.employeeRole.mapper;

import com.cmms.lite.employeeRole.dto.EmployeeRoleCreateRequest;
import com.cmms.lite.employeeRole.dto.EmployeeRoleResponse;
import com.cmms.lite.employeeRole.dto.EmployeeRoleUpdateRequest;
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
    EmployeeRole toEntity(EmployeeRoleCreateRequest request);

    EmployeeRoleResponse toResponse(EmployeeRole role);

    List<EmployeeRoleResponse> toResponseList(List<EmployeeRole> roles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromRequest(EmployeeRoleUpdateRequest request, @MappingTarget EmployeeRole role);
}