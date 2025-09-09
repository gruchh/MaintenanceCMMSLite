package com.cmms.lite.service;

import com.cmms.lite.api.dto.EmployeeDTOs;
import com.cmms.lite.core.entity.Address;
import com.cmms.lite.core.entity.Employee;
import com.cmms.lite.core.entity.EmployeeDetails;
import com.cmms.lite.core.entity.EmployeeRole;
import com.cmms.lite.core.mapper.EmployeeMapper;
import com.cmms.lite.core.repository.EmployeeRepository;
import com.cmms.lite.core.repository.EmployeeRoleRepository;
import com.cmms.lite.exception.EmployeeNotFoundException;
import com.cmms.lite.exception.EmployeeRoleNotFoundException;
import com.cmms.lite.exception.IllegalOperationException;
import com.cmms.lite.exception.UserNotFoundException;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final EmployeeMapper employeeMapper;

    private static final String EMPLOYEE_NOT_FOUND = "Pracownik o ID %d nie został znaleziony.";
    private static final String USER_NOT_FOUND = "Użytkownik o ID %d nie został znaleziony.";
    private static final String ROLE_NOT_FOUND = "Rola pracownika o ID %d nie została znaleziona.";

    @Transactional
    public EmployeeDTOs.Response createEmployee(EmployeeDTOs.CreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, request.userId())));

        if (employeeRepository.existsById(user.getId())) {
            throw new IllegalOperationException("Użytkownik o ID " + user.getId() + " jest już zarejestrowany jako pracownik.");
        }

        EmployeeRole role = employeeRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, request.roleId())));

        Employee employee = Employee.builder()
                .id(user.getId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .avatarUrl(request.avatarUrl())
                .user(user)
                .employeeRole(role)
                .build();

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeDTOs.Response getEmployeeById(Long employeeId) {
        Employee employee = getEmployeeByIdWithDetailsOrThrow(employeeId);
        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDTOs.Response> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAllWithSummary(pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDTOs.Response> searchEmployees(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEmployees(pageable);
        }
        return employeeRepository.searchByKeyword(keyword, pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional
    public EmployeeDTOs.Response updateEmployee(Long employeeId, EmployeeDTOs.UpdateRequest request) {
        Employee employee = getEmployeeByIdWithDetailsOrThrow(employeeId);
        updateBasicFields(employee, request);
        updateRole(employee, request.roleId());
        updateDetails(employee, request);
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId));
        }
        employeeRepository.deleteById(employeeId);
    }

    private void updateBasicFields(Employee employee, EmployeeDTOs.UpdateRequest request) {
        Optional.ofNullable(request.firstName()).ifPresent(employee::setFirstName);
        Optional.ofNullable(request.lastName()).ifPresent(employee::setLastName);
        Optional.ofNullable(request.avatarUrl()).ifPresent(employee::setAvatarUrl);
    }

    private void updateRole(Employee employee, Long roleId) {
        Optional.ofNullable(roleId).ifPresent(id -> {
            EmployeeRole role = employeeRoleRepository.findById(id)
                    .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, id)));
            employee.setEmployeeRole(role);
        });
    }

    private void updateDetails(Employee employee, EmployeeDTOs.UpdateRequest request) {
        if (isAnyDetailFieldPresent(request)) {
            EmployeeDetails details = Optional.ofNullable(employee.getEmployeeDetails()).orElseGet(() -> {
                EmployeeDetails newDetails = new EmployeeDetails();
                newDetails.setEmployee(employee);
                employee.setEmployeeDetails(newDetails);
                return newDetails;
            });

            Optional.ofNullable(request.phoneNumber()).ifPresent(details::setPhoneNumber);
            Optional.ofNullable(request.dateOfBirth()).ifPresent(details::setDateOfBirth);
            Optional.ofNullable(request.hireDate()).ifPresent(details::setHireDate);
            Optional.ofNullable(request.contractEndDate()).ifPresent(details::setContractEndDate);
            Optional.ofNullable(request.salary()).ifPresent(details::setSalary);
            Optional.ofNullable(request.educationLevel()).ifPresent(details::setEducationLevel);
            Optional.ofNullable(request.fieldOfStudy()).ifPresent(details::setFieldOfStudy);
            Optional.ofNullable(request.emergencyContactName()).ifPresent(details::setEmergencyContactName);
            Optional.ofNullable(request.emergencyContactPhone()).ifPresent(details::setEmergencyContactPhone);

            Optional.ofNullable(request.address()).ifPresent(addressRequest -> {
                Address address = Optional.ofNullable(details.getAddress()).orElseGet(() -> {
                    Address newAddress = new Address();
                    details.setAddress(newAddress);
                    return newAddress;
                });
                Optional.ofNullable(addressRequest.street()).ifPresent(address::setStreet);
                Optional.ofNullable(addressRequest.city()).ifPresent(address::setCity);
                Optional.ofNullable(addressRequest.postalCode()).ifPresent(address::setPostalCode);
                Optional.ofNullable(addressRequest.country()).ifPresent(address::setCountry);
            });
        }
    }

    private boolean isAnyDetailFieldPresent(EmployeeDTOs.UpdateRequest request) {
        return request.phoneNumber() != null || request.dateOfBirth() != null || request.hireDate() != null ||
                request.address() != null || request.contractEndDate() != null || request.salary() != null ||
                request.educationLevel() != null || request.fieldOfStudy() != null ||
                request.emergencyContactName() != null || request.emergencyContactPhone() != null;
    }

    private Employee getEmployeeByIdWithDetailsOrThrow(Long employeeId) {
        return employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId)));
    }
}