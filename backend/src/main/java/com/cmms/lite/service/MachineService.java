package com.cmms.lite.service;

import com.cmms.lite.api.dto.MachineDTOs;
import com.cmms.lite.core.entity.Machine;
import com.cmms.lite.core.mapper.MachineMapper;
import com.cmms.lite.core.repository.MachineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;

    private static final String NOT_FOUND_MESSAGE = "Machine not found with id: ";

    @Transactional(readOnly = true)
    public Page<MachineDTOs.Response> findAll(Pageable pageable) {
        Page<Machine> machinePage = machineRepository.findAll(pageable);
        return machinePage.map(machineMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MachineDTOs.Response findById(Long id) {
        return machineRepository.findById(id)
                .map(machineMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
    }

    @Transactional
    public MachineDTOs.Response save(MachineDTOs.CreateRequest createRequest) {
        Machine machine = machineMapper.toEntity(createRequest);
        Machine savedMachine = machineRepository.save(machine);
        return machineMapper.toResponse(savedMachine);
    }

    @Transactional
    public MachineDTOs.Response update(Long id, MachineDTOs.UpdateRequest updateRequest) {
        Machine existingMachine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
        machineMapper.updateEntityFromRequest(updateRequest, existingMachine);
        Machine updatedMachine = machineRepository.save(existingMachine);
        return machineMapper.toResponse(updatedMachine);
    }

    @Transactional
    public void delete(Long id) {
        if (!machineRepository.existsById(id)) {
            throw new EntityNotFoundException(NOT_FOUND_MESSAGE + id);
        }
        machineRepository.deleteById(id);
    }
}