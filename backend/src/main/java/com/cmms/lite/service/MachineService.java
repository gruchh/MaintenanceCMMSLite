package com.cmms.lite.service;

import com.cmms.lite.api.dto.MachineDTOs;
import com.cmms.lite.core.entity.Machine;
import com.cmms.lite.core.mapper.MachineMapper;
import com.cmms.lite.core.repository.MachineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MachineService {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;

    private static final String NOT_FOUND_MESSAGE = "Machine not found with id: ";

    @Transactional(readOnly = true)
    public Page<MachineDTOs.Response> getAllMachines(Pageable pageable) {
        return machineRepository.findAll(pageable)
                .map(machineMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MachineDTOs.Response> getAllMachinesAsList() {
        return machineRepository.findAll()
                .stream()
                .map(machineMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MachineDTOs.Response getMachineById(Long id) {
        Machine machine = getMachineByIdOrThrow(id);
        return machineMapper.toResponse(machine);
    }

    @Transactional
    public MachineDTOs.Response createMachine(MachineDTOs.CreateRequest request) {
        Machine machine = machineMapper.toEntity(request);
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public MachineDTOs.Response updateMachine(Long id, MachineDTOs.UpdateRequest request) {
        Machine machine = getMachineByIdOrThrow(id);
        machineMapper.updateEntityFromRequest(request, machine);
        return machineMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public void deleteMachine(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new EntityNotFoundException(NOT_FOUND_MESSAGE + id);
        }
        machineRepository.deleteById(id);
    }

    private Machine getMachineByIdOrThrow(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
    }
}