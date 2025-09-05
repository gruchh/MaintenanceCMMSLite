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
    private EmployeeDTOs.CreateRequest createRequest;
    private EmployeeDTOs.Response responseDTO;
    private EmployeeDTOs.UpdateRequest updateRequest;

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


        createRequest = new EmployeeDTOs.CreateRequest(1L, 1L);
        responseDTO = new EmployeeDTOs.Response(1L, "testuser", null, null, null, null, null, "Mechanic", null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null);
    }

    @Test
    void createEmployee_shouldCreateEmployeeSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeRepository.existsById(1L)).thenReturn(false);
        when(employeeRoleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        when(employeeMapper.toResponse(testEmployee)).thenReturn(responseDTO);

        EmployeeDTOs.Response result = employeeService.createEmployee(createRequest);

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

        EmployeeDTOs.Response result = employeeService.getEmployeeById(1L);

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
        EmployeeDTOs.AddressUpdateRequest addressRequest = new EmployeeDTOs.AddressUpdateRequest("Street", "City", "00-000", "Country");
        updateRequest = new EmployeeDTOs.UpdateRequest(null, "123456789", null, null, addressRequest, null, null, null, null, null, null);

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
        updateRequest = new EmployeeDTOs.UpdateRequest(null, null, null, null, null, null, null, null, null, null, null);
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