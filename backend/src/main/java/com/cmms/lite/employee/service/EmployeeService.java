package com.cmms.lite.employee.service;

import com.cmms.lite.employee.dto.CreateEmployeeDTO;
import com.cmms.lite.employee.dto.EmployeeResponseDTO;
import com.cmms.lite.employee.dto.UpdateEmployeeDTO;
import com.cmms.lite.employee.entity.Address;
import com.cmms.lite.employee.entity.Employee;
import com.cmms.lite.employee.entity.EmployeeDetails;
import com.cmms.lite.employee.exception.EmployeeNotFoundException;
import com.cmms.lite.employee.exception.EmployeeRoleNotFoundException;
import com.cmms.lite.employee.mapper.EmployeeMapper;
import com.cmms.lite.employee.repository.EmployeeRepository;
import com.cmms.lite.employee.repository.EmployeeRoleRepository;
import com.cmms.lite.employeeRole.entity.EmployeeRole;
import com.cmms.lite.exception.IllegalOperationException;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.exception.UserNotFoundException;
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
    public EmployeeResponseDTO createEmployee(CreateEmployeeDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, request.getUserId())));

        if (employeeRepository.existsById(user.getId())) {
            throw new IllegalOperationException("Użytkownik o ID " + user.getId() + " jest już zarejestrowany jako pracownik.");
        }

        EmployeeRole role = employeeRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, request.getRoleId())));

        Employee employee = Employee.builder()
                .id(user.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .avatarUrl(request.getAvatarUrl())
                .user(user)
                .employeeRole(role)
                .build();

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long employeeId) {
        Employee employee = getEmployeeByIdWithDetailsOrThrow(employeeId);
        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> findAllWithDetails(Pageable pageable) {
        return employeeRepository.findAllWithSummary(pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> searchEmployees(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllWithDetails(pageable);
        }
        return employeeRepository.searchByKeyword(keyword, pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long employeeId, UpdateEmployeeDTO request) {
        Employee employee = getEmployeeByIdWithDetailsOrThrow(employeeId);
        updateBasicFields(employee, request);
        updateRole(employee, request.getRoleId());
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

    private void updateBasicFields(Employee employee, UpdateEmployeeDTO request) {
        Optional.ofNullable(request.getFirstName()).ifPresent(employee::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(employee::setLastName);
        Optional.ofNullable(request.getAvatarUrl()).ifPresent(employee::setAvatarUrl);
    }

    private void updateRole(Employee employee, Long roleId) {
        Optional.ofNullable(roleId).ifPresent(id -> {
            EmployeeRole role = employeeRoleRepository.findById(id)
                    .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, id)));
            employee.setEmployeeRole(role);
        });
    }

    private void updateDetails(Employee employee, UpdateEmployeeDTO request) {
        if (isAnyDetailFieldPresent(request)) {
            EmployeeDetails details = Optional.ofNullable(employee.getEmployeeDetails()).orElseGet(() -> {
                EmployeeDetails newDetails = new EmployeeDetails();
                newDetails.setEmployee(employee);
                employee.setEmployeeDetails(newDetails);
                return newDetails;
            });

            Optional.ofNullable(request.getPhoneNumber()).ifPresent(details::setPhoneNumber);
            Optional.ofNullable(request.getDateOfBirth()).ifPresent(details::setDateOfBirth);
            Optional.ofNullable(request.getHireDate()).ifPresent(details::setHireDate);
            Optional.ofNullable(request.getContractEndDate()).ifPresent(details::setContractEndDate);
            Optional.ofNullable(request.getSalary()).ifPresent(details::setSalary);
            Optional.ofNullable(request.getEducationLevel()).ifPresent(details::setEducationLevel);
            Optional.ofNullable(request.getBrigade()).ifPresent(details::setBrigade);
            Optional.ofNullable(request.getFieldOfStudy()).ifPresent(details::setFieldOfStudy);
            Optional.ofNullable(request.getEmergencyContactName()).ifPresent(details::setEmergencyContactName);
            Optional.ofNullable(request.getEmergencyContactPhone()).ifPresent(details::setEmergencyContactPhone);

            Optional.ofNullable(request.getAddress()).ifPresent(addressRequest -> {
                Address address = Optional.ofNullable(details.getAddress()).orElseGet(() -> {
                    Address newAddress = new Address();
                    details.setAddress(newAddress);
                    return newAddress;
                });
                Optional.ofNullable(addressRequest.getStreet()).ifPresent(address::setStreet);
                Optional.ofNullable(addressRequest.getCity()).ifPresent(address::setCity);
                Optional.ofNullable(addressRequest.getPostalCode()).ifPresent(address::setPostalCode);
                Optional.ofNullable(addressRequest.getCountry()).ifPresent(address::setCountry);
            });
        }
    }

    private boolean isAnyDetailFieldPresent(UpdateEmployeeDTO request) {
        return request.getPhoneNumber() != null || request.getDateOfBirth() != null || request.getHireDate() != null ||
                request.getAddress() != null || request.getContractEndDate() != null || request.getSalary() != null ||
                request.getEducationLevel() != null || request.getFieldOfStudy() != null ||
                request.getEmergencyContactName() != null || request.getEmergencyContactPhone() != null;
    }

    private Employee getEmployeeByIdWithDetailsOrThrow(Long employeeId) {
        return employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId)));
    }
}