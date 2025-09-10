package com.cmms.lite.breakdown.mapper;

import com.cmms.lite.breakdown.dto.BreakdownResponseDTO;
import com.cmms.lite.breakdown.dto.UsedPartBreakdownDTO;
import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
import com.cmms.lite.machine.mapper.MachineMapper;
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
    BreakdownResponseDTO toResponse(Breakdown breakdown);
    List<BreakdownResponseDTO> toResponseList(List<Breakdown> breakdowns);
    UsedPartBreakdownDTO toUsedPartResponse(BreakdownUsedParts usedPart);
}