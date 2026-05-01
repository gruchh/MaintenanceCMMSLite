package com.cmms.lite.shiftSchedule;

import com.cmms.lite.shiftSchedule.dto.GenerateShiftScheduleDTO;
import com.cmms.lite.shiftSchedule.dto.ShiftScheduleResponseDTO;
import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import com.cmms.lite.shiftSchedule.exception.ShiftScheduleNotFoundException;
import com.cmms.lite.shiftSchedule.factory.ShiftEntryFactory;
import com.cmms.lite.shiftSchedule.mapper.ShiftScheduleMapper;
import com.cmms.lite.shiftSchedule.repository.ShiftEntryRepository;
import com.cmms.lite.shiftSchedule.repository.ShiftScheduleRepository;
import com.cmms.lite.shiftSchedule.service.ShiftScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftScheduleServiceTest {

    @Mock ShiftScheduleRepository scheduleRepository;
    @Mock ShiftEntryRepository entryRepository;
    @Mock ShiftScheduleMapper shiftScheduleMapper;
    @Mock ShiftEntryFactory shiftEntryFactory;

    @InjectMocks
    ShiftScheduleService service;

    @Test
    void createShiftSchedule_withNullDays_calls112TimesFactory() {
        GenerateShiftScheduleDTO req = new GenerateShiftScheduleDTO();
        req.setStartDate(LocalDate.of(2025, 1, 1));
        req.setDays(null);

        when(scheduleRepository.save(any())).thenReturn(new ShiftSchedule());
        when(shiftEntryFactory.create(any(), any(), any(), anyInt())).thenReturn(new ShiftEntry());

        service.createShiftSchedule(req);

        verify(shiftEntryFactory, times(112)).create(any(), any(), any(), anyInt());
    }

    @Test
    void createShiftSchedule_callsSaveAllOnce() {
        GenerateShiftScheduleDTO req = new GenerateShiftScheduleDTO();
        req.setStartDate(LocalDate.of(2025, 1, 1));
        req.setDays(28);

        when(scheduleRepository.save(any())).thenReturn(new ShiftSchedule());
        when(shiftEntryFactory.create(any(), any(), any(), anyInt())).thenReturn(new ShiftEntry());

        service.createShiftSchedule(req);

        verify(entryRepository, times(1)).saveAll(any());
    }

    @Test
    void createShiftSchedule_with7Days_calls28TimesFactory() {
        GenerateShiftScheduleDTO req = new GenerateShiftScheduleDTO();
        req.setStartDate(LocalDate.of(2025, 1, 1));
        req.setDays(7);

        when(scheduleRepository.save(any())).thenReturn(new ShiftSchedule());
        when(shiftEntryFactory.create(any(), any(), any(), anyInt())).thenReturn(new ShiftEntry());

        service.createShiftSchedule(req);

        verify(shiftEntryFactory, times(28)).create(any(), any(), any(), anyInt());
    }

    @Test
    void getShiftScheduleById_nonExistentId_throwsShiftScheduleNotFoundException() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getShiftScheduleById(99L))
                .isInstanceOf(ShiftScheduleNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getShiftScheduleById_existingId_delegatesToMapper() {
        Long id = 1L;
        ShiftSchedule schedule = new ShiftSchedule();

        ShiftScheduleResponseDTO fakeResponse = new ShiftScheduleResponseDTO(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(28),
                List.of()
        );

        when(scheduleRepository.findById(id)).thenReturn(Optional.of(schedule));
        when(shiftScheduleMapper.toResponse(schedule)).thenReturn(fakeResponse);
        service.getShiftScheduleById(id);
        verify(shiftScheduleMapper, times(1)).toResponse(schedule);
    }
}