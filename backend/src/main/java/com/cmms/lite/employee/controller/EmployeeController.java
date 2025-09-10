package com.cmms.lite.employee.controller;

import com.cmms.lite.employee.EmployeeDTOs;
import com.cmms.lite.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/employees", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Employees", description = "API do zarządzania pracownikami")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDTOs.Response> createEmployee(@Valid @RequestBody EmployeeDTOs.CreateRequest request) {
        EmployeeDTOs.Response createdEmployee = employeeService.createEmployee(request);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTOs.Response> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EmployeeDTOs.Response>> getAllEmployees(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(employeeService.searchEmployees(search, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDTOs.Response> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTOs.UpdateRequest request
    ) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}