package com.cmms.lite.machine;

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
    public Page<MachineDTOs.Response> getAllMachines(Pageable pageable) {
        return machineRepository.findAll(pageable).map(machineMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MachineDTOs.Response> getAllMachinesAsList() {
        return machineRepository.findAll().stream().map(machineMapper::toResponse).toList();
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
            throw new MachineNotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
        machineRepository.deleteById(id);
    }

    private Machine getMachineByIdOrThrow(Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new MachineNotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }
}