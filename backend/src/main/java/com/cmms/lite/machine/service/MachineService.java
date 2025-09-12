package com.cmms.lite.machine.service;

import com.cmms.lite.machine.dto.CreateMachineDTO;
import com.cmms.lite.machine.dto.MachineResponseDTO;
import com.cmms.lite.machine.dto.UpdateMachineDTO;
import com.cmms.lite.machine.entity.Machine;
import com.cmms.lite.machine.exception.MachineNotFoundException;
import com.cmms.lite.machine.mapper.MachineMapper;
import com.cmms.lite.machine.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;

    private static final String NOT_FOUND_MESSAGE = "Maszyna o ID %d nie została znaleziona.";

    @Transactional(readOnly = true)
    public Page<MachineResponseDTO> getAllMachines(Pageable pageable) {
        return machineRepository.findAll(pageable).map(machineMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MachineResponseDTO> getAllMachinesAsList() {
        return machineRepository.findAll().stream().map(machineMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MachineResponseDTO getMachineById(Long id) {
        Machine machine = getMachineByIdOrThrow(id);
        return machineMapper.toResponse(machine);
    }

    @Transactional
    public MachineResponseDTO createMachine(CreateMachineDTO request) {
        Machine machine = machineMapper.toEntity(request);
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public MachineResponseDTO updateMachine(Long id, UpdateMachineDTO request) {
        Machine machine = getMachineByIdOrThrow(id);
        machineMapper.updateEntityFromRequest(request, machine);
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public void deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new MachineNotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
        machineRepository.deleteById(id);
    }

    private Machine getMachineByIdOrThrow(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new MachineNotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }
}