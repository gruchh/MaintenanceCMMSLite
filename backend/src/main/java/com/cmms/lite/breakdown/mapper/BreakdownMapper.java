package com.cmms.lite.breakdown.mapper;

import com.cmms.lite.breakdown.BreakdownDTOs;
import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
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