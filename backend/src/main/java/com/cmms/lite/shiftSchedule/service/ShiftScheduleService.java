package com.cmms.lite.shiftSchedule.service;

import com.cmms.lite.shiftSchedule.dto.GenerateShiftScheduleDTO;
import com.cmms.lite.shiftSchedule.dto.ShiftScheduleResponseDTO;
import com.cmms.lite.shiftSchedule.entity.BrigadeType;
import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import com.cmms.lite.shiftSchedule.entity.ShiftType;
import com.cmms.lite.shiftSchedule.mapper.ShiftScheduleMapper;
import com.cmms.lite.shiftSchedule.repository.ShiftEntryRepository;
import com.cmms.lite.shiftSchedule.repository.ShiftScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShiftScheduleService {

    private final ShiftScheduleRepository scheduleRepository;
    private final ShiftEntryRepository entryRepository;
    private final ShiftScheduleMapper shiftScheduleMapper;

    private static final String[] BASE_PATTERN = new String[]{
            "DDWWDDD",
            "WWDDWWW",
            "NNWWNNN",
            "WWNNWWW"
    };

    private static final Map<BrigadeType, String[]> BRIGADE_PATTERNS = Map.of(
            BrigadeType.A, new String[]{BASE_PATTERN[0], BASE_PATTERN[1], BASE_PATTERN[2], BASE_PATTERN[3]},
            BrigadeType.B, new String[]{BASE_PATTERN[1], BASE_PATTERN[2], BASE_PATTERN[3], BASE_PATTERN[0]},
            BrigadeType.C, new String[]{BASE_PATTERN[2], BASE_PATTERN[3], BASE_PATTERN[0], BASE_PATTERN[1]},
            BrigadeType.D, new String[]{BASE_PATTERN[3], BASE_PATTERN[0], BASE_PATTERN[1], BASE_PATTERN[2]}
    );

    private static final Map<Character, ShiftType> CHAR_TO_SHIFT = Map.of(
            'D', ShiftType.DAY,
            'N', ShiftType.NIGHT,
            'W', ShiftType.OFF
    );

    @Transactional
    public ShiftScheduleResponseDTO createShiftSchedule(GenerateShiftScheduleDTO req) {
        int days = (req.getDays() == null ? 28 : req.getDays());
        LocalDate start = req.getStartDate();
        LocalDate end = start.plusDays(days - 1);

        ShiftSchedule schedule = ShiftSchedule.builder()
                .startDate(start)
                .endDate(end)
                .build();

        schedule = scheduleRepository.save(schedule);

        List<ShiftEntry> batch = new ArrayList<>(days * 4);

        for (int i = 0; i < days; i++) {
            LocalDate date = start.plusDays(i);
            int week = (i / 7) % 4;
            int day = i % 7;

            for (BrigadeType brigade : BrigadeType.values()) {
                String[] patterns = BRIGADE_PATTERNS.get(brigade);
                char symbol = patterns[week].charAt(day);
                ShiftType shiftType = CHAR_TO_SHIFT.get(symbol);

                ShiftEntry entry = ShiftEntry.builder()
                        .schedule(schedule)
                        .workDate(date)
                        .brigadeType(brigade)
                        .shiftType(shiftType)
                        .build();

                batch.add(entry);
            }
        }

        entryRepository.saveAll(batch);
        schedule.getEntries().addAll(batch);

        return shiftScheduleMapper.toResponse(schedule);
    }

    @Transactional(readOnly = true)
    public ShiftScheduleResponseDTO getShiftScheduleById(Long id) {
        ShiftSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Harmonogram o ID %d nie istnieje.".formatted(id)));

        return shiftScheduleMapper.toResponse(schedule);
    }
}