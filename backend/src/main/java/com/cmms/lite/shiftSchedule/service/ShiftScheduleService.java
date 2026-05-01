package com.cmms.lite.shiftSchedule.service;

import com.cmms.lite.shiftSchedule.dto.GenerateShiftScheduleDTO;
import com.cmms.lite.shiftSchedule.dto.ShiftScheduleResponseDTO;
import com.cmms.lite.shiftSchedule.entity.BrigadeType;
import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import com.cmms.lite.shiftSchedule.exception.ShiftScheduleNotFoundException;
import com.cmms.lite.shiftSchedule.factory.ShiftEntryFactory;
import com.cmms.lite.shiftSchedule.mapper.ShiftScheduleMapper;
import com.cmms.lite.shiftSchedule.repository.ShiftEntryRepository;
import com.cmms.lite.shiftSchedule.repository.ShiftScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftScheduleService {

    private final ShiftScheduleRepository scheduleRepository;
    private final ShiftEntryRepository entryRepository;
    private final ShiftScheduleMapper shiftScheduleMapper;
    private final ShiftEntryFactory shiftEntryFactory;

    @Transactional
    public ShiftScheduleResponseDTO createShiftSchedule(GenerateShiftScheduleDTO req) {
        int days  = (req.getDays() == null ? 28 : req.getDays());
        LocalDate start = req.getStartDate();

        ShiftSchedule schedule = scheduleRepository.save(
                ShiftSchedule.builder()
                        .startDate(start)
                        .endDate(start.plusDays(days - 1))
                        .build()
        );

        List<ShiftEntry> batch = new ArrayList<>(days * 4);

        for (int i = 0; i < days; i++) {
            LocalDate date = start.plusDays(i);
            for (BrigadeType brigade : BrigadeType.values()) {
                batch.add(shiftEntryFactory.create(schedule, date, brigade, i));
            }
        }

        entryRepository.saveAll(batch);
        schedule.getEntries().addAll(batch);

        return shiftScheduleMapper.toResponse(schedule);
    }

    @Transactional(readOnly = true)
    public ShiftScheduleResponseDTO getShiftScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .map(shiftScheduleMapper::toResponse)
                .orElseThrow(() -> new ShiftScheduleNotFoundException(
                        "Schedule with ID %d does not exist.".formatted(id)));
    }
}