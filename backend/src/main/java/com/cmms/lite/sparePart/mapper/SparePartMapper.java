package com.cmms.lite.sparePart.mapper;

import com.cmms.lite.sparePart.dto.CreateSparePartDTO;
import com.cmms.lite.sparePart.dto.SparePartResponseDTO;
import com.cmms.lite.sparePart.dto.UpdateSparePartDTO;
import com.cmms.lite.sparePart.entity.SparePart;
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
    SparePart toEntity(CreateSparePartDTO request);

    SparePartResponseDTO toResponse(SparePart sparePart);

    List<SparePartResponseDTO> toResponseList(List<SparePart> spareParts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "breakdownUsedPartsList", ignore = true)
    void updateEntityFromRequest(UpdateSparePartDTO request, @MappingTarget SparePart sparePart);
}