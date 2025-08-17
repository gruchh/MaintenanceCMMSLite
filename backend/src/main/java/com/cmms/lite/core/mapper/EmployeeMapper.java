package com.cmms.lite.core.mapper;

import com.cmms.lite.api.dto.EmployeeDTOs;
import com.cmms.lite.core.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    @Mapping(source = "employeeRole.name", target = "role")
    @Mapping(source = "retirementDate", target = "retirementDate")
    @Mapping(source = "employeeDetails.hireDate", target = "hireDate")
    @Mapping(source = "employeeDetails.phoneNumber", target = "phoneNumber")
    @Mapping(source = "employeeDetails.age", target = "age")
    @Mapping(source = "employeeDetails.salary", target = "salary")
    @Mapping(source = "employeeDetails.educationLevel", target = "educationLevel")
    @Mapping(source = "employeeDetails.fieldOfStudy", target = "fieldOfStudy")
    @Mapping(target = "street", expression = "java(employee.getEmployeeDetails() != null && employee.getEmployeeDetails().getAddress() != null ? employee.getEmployeeDetails().getAddress().getStreet() : null)")
    @Mapping(target = "city", expression = "java(employee.getEmployeeDetails() != null && employee.getEmployeeDetails().getAddress() != null ? employee.getEmployeeDetails().getAddress().getCity() : null)")
    @Mapping(target = "postalCode", expression = "java(employee.getEmployeeDetails() != null && employee.getEmployeeDetails().getAddress() != null ? employee.getEmployeeDetails().getAddress().getPostalCode() : null)")
    @Mapping(target = "country", expression = "java(employee.getEmployeeDetails() != null && employee.getEmployeeDetails().getAddress() != null ? employee.getEmployeeDetails().getAddress().getCountry() : null)")
    EmployeeDTOs.Response toResponse(Employee employee);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    @Mapping(source = "employeeRole.name", target = "role")
    EmployeeDTOs.SummaryResponse toSummaryResponse(Employee employee);
}