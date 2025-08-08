package com.cmms.lite.core.mapper;

import com.cmms.lite.api.dto.MachineDTOs;
import com.cmms.lite.core.entity.Machine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface MachineMapper {

    Machine toEntity(MachineDTOs.CreateRequest request);
    MachineDTOs.Response toResponse(Machine machine);
    List<MachineDTOs.Response> toResponseList(List<Machine> machines);
    void updateEntityFromRequest(MachineDTOs.UpdateRequest request, @MappingTarget Machine machine);
    MachineDTOs.SummaryResponse toSummaryResponse(Machine machine);
}