package com.cmms.lite.machine.mapper;

import com.cmms.lite.machine.dto.CreateMachineDTO;
import com.cmms.lite.machine.dto.MachineResponseDTO;
import com.cmms.lite.machine.dto.MachineSummaryDTO;
import com.cmms.lite.machine.dto.UpdateMachineDTO;
import com.cmms.lite.machine.entity.Machine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MachineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownsList", ignore = true)
    Machine toEntity(CreateMachineDTO request);

    MachineResponseDTO toResponse(Machine machine);

    List<MachineResponseDTO> toResponseList(List<Machine> machines);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownsList", ignore = true)
    void updateEntityFromRequest(UpdateMachineDTO request, @MappingTarget Machine machine);

    MachineSummaryDTO toSummaryResponse(Machine machine);
}