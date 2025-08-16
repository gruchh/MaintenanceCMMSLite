package com.cmms.lite.core.mapper;

import com.cmms.lite.api.dto.EmployeeDTOs;
import com.cmms.lite.core.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmployeeMapper {

    @Mapping(source = "user.username", target = "username") // Mapujemy username na username
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "employeeRole.name", target = "role")
    EmployeeDTOs.Response toResponse(Employee employee);

    List<EmployeeDTOs.Response> toResponseList(List<Employee> employees);

    @Mapping(source = "user.username", target = "username") // Mapujemy username na username
    EmployeeDTOs.SummaryResponse toSummaryResponse(Employee employee);
}