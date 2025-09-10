package com.cmms.lite.service;

import com.cmms.lite.machine.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineServiceTest {

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private MachineMapper machineMapper;

    @InjectMocks
    private MachineService machineService;

    private Machine testMachine;
    private MachineDTOs.Response responseDTO;
    private MachineDTOs.CreateRequest createRequest;
    private MachineDTOs.UpdateRequest updateRequest;


    @BeforeEach
    void setUp() {
        testMachine = new Machine();
        testMachine.setId(1L);
        testMachine.setCode("M-001");
        testMachine.setFullName("Test Machine");

        responseDTO = new MachineDTOs.Response(1L, "M-001", "Test Machine", "SN123", "Manufacturer", LocalDate.now(), "Desc");
        createRequest = new MachineDTOs.CreateRequest("M-001", "Test Machine", "SN123", "Manufacturer", LocalDate.now(), "Desc");
        updateRequest = new MachineDTOs.UpdateRequest("M-002", "Updated Machine", "SN124", "New Manufacturer", LocalDate.now(), "Updated Desc");
    }

    @Test
    void getMachineById_shouldReturnMachine_whenMachineExists() {
        when(machineRepository.findById(1L)).thenReturn(Optional.of(testMachine));
        when(machineMapper.toResponse(testMachine)).thenReturn(responseDTO);

        MachineDTOs.Response result = machineService.getMachineById(1L);

        assertThat(result).isNotNull();
        assertEquals(1L, result.id());
    }

    @Test
    void getMachineById_shouldThrowMachineNotFoundException_whenMachineDoesNotExist() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MachineNotFoundException.class, () -> machineService.getMachineById(1L));
    }

    @Test
    void getAllMachines_shouldReturnMachinePageSuccessfully() {
        Pageable pageable = Pageable.unpaged();
        Page<Machine> machinePage = new PageImpl<>(List.of(testMachine));
        when(machineRepository.findAll(pageable)).thenReturn(machinePage);
        when(machineMapper.toResponse(any(Machine.class))).thenReturn(responseDTO);

        Page<MachineDTOs.Response> result = machineService.getAllMachines(pageable);

        assertThat(result).isNotNull();
        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent().get(0).code()).isEqualTo("M-001");
    }

    @Test
    void createMachine_shouldCreateAndReturnMachine() {
        when(machineMapper.toEntity(createRequest)).thenReturn(testMachine);
        when(machineRepository.save(testMachine)).thenReturn(testMachine);
        when(machineMapper.toResponse(testMachine)).thenReturn(responseDTO);

        MachineDTOs.Response result = machineService.createMachine(createRequest);

        assertThat(result).isNotNull();
        assertEquals(createRequest.code(), result.code());
        verify(machineRepository, times(1)).save(testMachine);
    }

    @Test
    void updateMachine_shouldUpdateMachineSuccessfully_whenMachineExists() {
        Machine updatedMachine = new Machine();

        when(machineRepository.findById(1L)).thenReturn(Optional.of(testMachine));
        doNothing().when(machineMapper).updateEntityFromRequest(updateRequest, testMachine);
        when(machineRepository.save(testMachine)).thenReturn(updatedMachine);
        when(machineMapper.toResponse(updatedMachine)).thenReturn(responseDTO);

        MachineDTOs.Response result = machineService.updateMachine(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(machineMapper, times(1)).updateEntityFromRequest(updateRequest, testMachine);
        verify(machineRepository, times(1)).save(testMachine);
    }

    @Test
    void updateMachine_shouldThrowMachineNotFoundException_whenMachineDoesNotExist() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MachineNotFoundException.class, () -> machineService.updateMachine(1L, updateRequest));
        verify(machineRepository, never()).save(any());
    }

    @Test
    void deleteMachine_shouldDeleteMachineSuccessfully_whenMachineExists() {
        when(machineRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> machineService.deleteMachine(1L));

        verify(machineRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMachine_shouldThrowMachineNotFoundException_whenMachineDoesNotExist() {
        when(machineRepository.existsById(1L)).thenReturn(false);

        assertThrows(MachineNotFoundException.class, () -> machineService.deleteMachine(1L));
        verify(machineRepository, never()).deleteById(any());
    }
}