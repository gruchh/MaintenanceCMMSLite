package com.cmms.lite.service;

import com.cmms.lite.api.dto.ShiftScheduleDTOs;
import com.cmms.lite.core.entity.*;
import com.cmms.lite.core.repository.ShiftEntryRepository;
import com.cmms.lite.core.repository.ShiftScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShiftScheduleService {

    private final ShiftScheduleRepository scheduleRepository;
    private final ShiftEntryRepository entryRepository;

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
    public ShiftScheduleDTOs.ScheduleResponse generate(ShiftScheduleDTOs.GenerateRequest req) {
        int days = (req.days() == null ? 28 : req.days());
        LocalDate start = req.startDate();
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

        return toResponse(schedule);
    }

    @Transactional(readOnly = true)
    public ShiftScheduleDTOs.ScheduleResponse getById(Long id) {
        ShiftSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Harmonogram o ID %d nie istnieje.".formatted(id)));
        return toResponse(schedule);
    }

    private ShiftScheduleDTOs.ScheduleResponse toResponse(ShiftSchedule s) {
        var entries = s.getEntries().stream()
                .map(e -> new ShiftScheduleDTOs.EntryResponse(
                        e.getId(),
                        e.getWorkDate(),
                        e.getBrigadeType(),
                        e.getShiftType()
                ))
                .toList();

        return new ShiftScheduleDTOs.ScheduleResponse(
                s.getId(),
                s.getStartDate(),
                s.getEndDate(),
                entries
        );
    }
}
