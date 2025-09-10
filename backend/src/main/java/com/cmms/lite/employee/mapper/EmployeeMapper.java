package com.cmms.lite.employee.mapper;

import com.cmms.lite.employee.EmployeeDTOs;
import com.cmms.lite.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(expression = "java(employee.getFirstName() + \" \" + employee.getLastName())", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    @Mapping(source = "employeeRole.name", target = "role")
    @Mapping(source = "retirementDate", target = "retirementDate")
    @Mapping(source = "employeeDetails.hireDate", target = "hireDate")
    @Mapping(source = "employeeDetails.phoneNumber", target = "phoneNumber")
    @Mapping(source = "employeeDetails.age", target = "age")
    @Mapping(source = "employeeDetails.salary", target = "salary")
    @Mapping(source = "employeeDetails.educationLevel", target = "educationLevel")
    @Mapping(source = "employeeDetails.fieldOfStudy", target = "fieldOfStudy")
    @Mapping(source = "employeeDetails.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "employeeDetails.contractEndDate", target = "contractEndDate")
    @Mapping(source = "employeeDetails.emergencyContactName", target = "emergencyContactName")
    @Mapping(source = "employeeDetails.emergencyContactPhone", target = "emergencyContactPhone")
    @Mapping(source = "employeeDetails.address.street", target = "street")
    @Mapping(source = "employeeDetails.address.city", target = "city")
    @Mapping(source = "employeeDetails.address.postalCode", target = "postalCode")
    @Mapping(source = "employeeDetails.address.country", target = "country")
    EmployeeDTOs.Response toResponse(Employee employee);

    @Mapping(source = "user.username", target = "username")
    @Mapping(expression = "java(employee.getFirstName() + \" \" + employee.getLastName())", target = "fullName")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    @Mapping(source = "employeeRole.name", target = "role")
    EmployeeDTOs.SummaryResponse toSummaryResponse(Employee employee);
}