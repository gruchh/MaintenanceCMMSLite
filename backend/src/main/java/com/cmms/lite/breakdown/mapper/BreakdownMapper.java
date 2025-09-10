package com.cmms.lite.breakdown;

import com.cmms.lite.machine.MachineMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {MachineMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BreakdownMapper {

    @Mapping(source = "usedPartsList", target = "usedParts")
    BreakdownDTOs.Response toResponse(Breakdown breakdown);
    List<BreakdownDTOs.Response> toResponseList(List<Breakdown> breakdowns);
    BreakdownDTOs.UsedPartResponse toUsedPartResponse(BreakdownUsedParts usedPart);
}