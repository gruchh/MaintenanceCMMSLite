package com.cmms.lite.service;

import com.cmms.lite.employee.dto.CreateEmployeeDTO;
import com.cmms.lite.employee.dto.EmployeeResponseDTO;
import com.cmms.lite.employee.dto.UpdateEmployeeAddressDTO;
import com.cmms.lite.employee.dto.UpdateEmployeeDTO;
import com.cmms.lite.employee.entity.Address;
import com.cmms.lite.employee.entity.Employee;
import com.cmms.lite.employee.entity.EmployeeDetails;
import com.cmms.lite.employee.exception.EmployeeNotFoundException;
import com.cmms.lite.employee.exception.EmployeeRoleNotFoundException;
import com.cmms.lite.employee.mapper.EmployeeMapper;
import com.cmms.lite.employee.repository.EmployeeRepository;
import com.cmms.lite.employee.repository.EmployeeRoleRepository;
import com.cmms.lite.employee.service.EmployeeService;
import com.cmms.lite.employeeRole.entity.EmployeeRole;
import com.cmms.lite.exception.IllegalOperationException;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.exception.UserNotFoundException;
import com.cmms.lite.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeRoleRepository employeeRoleRepository;
    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private User testUser;
    private EmployeeRole testRole;
    private Employee testEmployee;
    private CreateEmployeeDTO createRequest;
    private EmployeeResponseDTO responseDTO;
    private UpdateEmployeeDTO updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRole = new EmployeeRole();
        testRole.setId(1L);
        testRole.setName("Mechanic");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setUser(testUser);
        testEmployee.setEmployeeRole(testRole);
        EmployeeDetails details = new EmployeeDetails();
        details.setAddress(new Address());
        testEmployee.setEmployeeDetails(details);


        createRequest = new CreateEmployeeDTO(1L, "Jan", "Kowalski", "https://example.com/avatar.jpg", 1L);
        responseDTO = new EmployeeResponseDTO(1L, "testuser", null, null, null, null, null, "Mechanic", null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null);
    }

    @Test
    void createEmployee_shouldCreateEmployeeSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeRepository.existsById(1L)).thenReturn(false);
        when(employeeRoleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        when(employeeMapper.toResponse(testEmployee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.createEmployee(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> employeeService.createEmployee(createRequest));
    }

    @Test
    void createEmployee_shouldThrowException_whenUserIsAlreadyEmployee() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalOperationException.class, () -> employeeService.createEmployee(createRequest));
    }

    @Test
    void createEmployee_shouldThrowEmployeeRoleNotFoundException_whenRoleDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeRepository.existsById(1L)).thenReturn(false);
        when(employeeRoleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeRoleNotFoundException.class, () -> employeeService.createEmployee(createRequest));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee_whenEmployeeExists() {
        when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toResponse(testEmployee)).thenReturn(responseDTO);

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {
        when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void updateEmployee_shouldUpdateDetailsSuccessfully() {
        UpdateEmployeeAddressDTO addressRequest = new UpdateEmployeeAddressDTO("Street", "City", "00-000", "Country");
        updateRequest = new UpdateEmployeeDTO( null,null,null, null, "123456789",null,null, addressRequest, null,null,null,null, null,null);

        when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        when(employeeMapper.toResponse(testEmployee)).thenReturn(responseDTO);

        employeeService.updateEmployee(1L, updateRequest);

        assertThat(testEmployee.getEmployeeDetails()).isNotNull();
        assertThat(testEmployee.getEmployeeDetails().getAddress()).isNotNull();
        assertThat(testEmployee.getEmployeeDetails().getAddress().getCity()).isEqualTo("City");
        assertThat(testEmployee.getEmployeeDetails().getPhoneNumber()).isEqualTo("123456789");
        verify(employeeRepository, times(1)).save(testEmployee);
    }

    @Test
    void updateEmployee_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {
        updateRequest = new UpdateEmployeeDTO( null,null,null, null, "123456789",null,null, null, null,null,null,null, null,null);

        when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(1L, updateRequest));
    }

    @Test
    void deleteEmployee_shouldDeleteEmployeeSuccessfully_whenEmployeeExists() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployee_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }
}