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
import com.cmms.lite.exception.UserNotFoundException;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new EmployeeRoleNotFoundException("Użytkownik o ID " + user.getId() + " jest już zarejestrowany jako pracownik.");
        }

        EmployeeRole role = employeeRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, request.roleId())));

        Employee employee = new Employee();
        employee.setId(user.getId());
        employee.setUser(user);
        employee.setEmployeeRole(role);

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeDTOs.Response getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId)));
        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDTOs.SummaryResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAllWithSummary(pageable)
                .map(employeeMapper::toSummaryResponse);
    }

    @Transactional
    public EmployeeDTOs.Response updateEmployeeDetails(Long employeeId, EmployeeDTOs.DetailsRequest request) {
        Employee employee = getEmployeeByIdWithDetailsOrThrow(employeeId);

        EmployeeDetails details = employee.getEmployeeDetails();
        if (details == null) {
            details = new EmployeeDetails();
            employee.setEmployeeDetails(details);
            details.setEmployee(employee);
        }

        Address address = details.getAddress();
        if (address == null) {
            address = new Address();
            details.setAddress(address);
        }
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());

        details.setPhoneNumber(request.phoneNumber());
        details.setDateOfBirth(request.dateOfBirth());
        details.setHireDate(request.hireDate());
        details.setContractEndDate(request.contractEndDate());
        details.setSalary(request.salary());
        details.setEducationLevel(request.educationLevel());
        details.setFieldOfStudy(request.fieldOfStudy());
        details.setEmergencyContactName(request.emergencyContactName());
        details.setEmergencyContactPhone(request.emergencyContactPhone());

        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId));
        }
        employeeRepository.deleteById(employeeId);
    }

    private Employee getEmployeeByIdWithDetailsOrThrow(Long employeeId) {
        return employeeRepository.findByIdWithDetails(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId)));
    }
}