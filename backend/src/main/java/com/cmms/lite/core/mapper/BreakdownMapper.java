package com.cmms.lite.core.mapper;

import com.cmms.lite.api.dto.BreakdownDTOs;
import com.cmms.lite.core.entity.Breakdown;
import com.cmms.lite.core.entity.BreakdownUsedParts;
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