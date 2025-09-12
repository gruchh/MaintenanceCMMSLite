package com.cmms.lite.shiftSchedule.mapper;

import com.cmms.lite.shiftSchedule.dto.ShiftEntryResponseDTO;
import com.cmms.lite.shiftSchedule.dto.ShiftScheduleResponseDTO;
import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShiftScheduleMapper {

    ShiftScheduleResponseDTO toResponse(ShiftSchedule schedule);

    @Mapping(source = "workDate", target = "date")
    @Mapping(source = "brigadeType", target = "brigade")
    @Mapping(source = "shiftType", target = "shift")

    ShiftEntryResponseDTO toEntryResponse(ShiftEntry entry);
}