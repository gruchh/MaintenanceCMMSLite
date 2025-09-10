package com.cmms.lite.machine.mapper;

import com.cmms.lite.machine.MachineDTOs;
import com.cmms.lite.machine.MachineUpdateRequest;
import com.cmms.lite.machine.dto.MachineDetailsResponse;
import com.cmms.lite.machine.dto.MachineSummaryResponse;
import com.cmms.lite.machine.entity.Machine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MachineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownsList", ignore = true)
    Machine toEntity(MachineDTOs.CreateRequest request);

    MachineDetailsResponse toResponse(Machine machine);

    List<MachineDetailsResponse> toResponseList(List<Machine> machines);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownsList", ignore = true)
    void updateEntityFromRequest(MachineUpdateRequest request, @MappingTarget Machine machine);

    MachineSummaryResponse toSummaryResponse(Machine machine);
}