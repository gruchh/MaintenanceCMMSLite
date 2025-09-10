package com.cmms.lite.sparePart;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SparePartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownUsedPartsList", ignore = true)
    SparePart toEntity(SparePartDTOs.CreateRequest request);

    SparePartDTOs.Response toResponse(SparePart sparePart);

    List<SparePartDTOs.Response> toResponseList(List<SparePart> spareParts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownUsedPartsList", ignore = true)
    void updateEntityFromRequest(SparePartDTOs.UpdateRequest request, @MappingTarget SparePart sparePart);
}