package com.cmms.lite.core.mapper;

import com.cmms.lite.api.dto.EmployeeRoleDTOs;
import com.cmms.lite.core.entity.EmployeeRole;
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
    EmployeeRole toEntity(EmployeeRoleDTOs.CreateRequest request);

    EmployeeRoleDTOs.Response toResponse(EmployeeRole role);

    List<EmployeeRoleDTOs.Response> toResponseList(List<EmployeeRole> roles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromRequest(EmployeeRoleDTOs.UpdateRequest request, @MappingTarget EmployeeRole role);
}