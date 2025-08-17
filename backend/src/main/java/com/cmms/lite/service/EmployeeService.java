package com.cmms.lite.service;

import com.cmms.lite.api.dto.EmployeeDTOs;
import com.cmms.lite.core.entity.Employee;
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
        EmployeeRole role = employeeRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, request.roleId())));

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setEmployeeRole(role);

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeDTOs.Response getEmployeeById(Long employeeId) {
        Employee employee = getEmployeeByIdOrThrow(employeeId);
        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDTOs.SummaryResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toSummaryResponse);
    }

    @Transactional
    public EmployeeDTOs.Response updateEmployeeRole(Long employeeId, EmployeeDTOs.UpdateRequest request) {
        Employee employee = getEmployeeByIdOrThrow(employeeId);

        EmployeeRole newRole = employeeRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new EmployeeRoleNotFoundException(String.format(ROLE_NOT_FOUND, request.roleId())));

        employee.setEmployeeRole(newRole);

        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = getEmployeeByIdOrThrow(employeeId);
        employeeRepository.delete(employee);
    }

    private Employee getEmployeeByIdOrThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(String.format(EMPLOYEE_NOT_FOUND, employeeId)));
    }
}