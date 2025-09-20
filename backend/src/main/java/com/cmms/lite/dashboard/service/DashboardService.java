package com.cmms.lite.dashboard.service;

import com.cmms.lite.breakdown.dto.BreakdownPerformanceIndicatorDTO;
import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.dashboard.dto.DashboardDTO;
import com.cmms.lite.machine.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final BreakdownRepository breakdownRepository;
    private final MachineRepository machineRepository;

    @Transactional(readOnly = true)
    public List<DashboardDTO.FactoryStatsDTO> getWeeklyPerformance() {
        long totalMachines = machineRepository.count();
        List<LocalDate> lastSevenDays = getLastSevenDays();

        if (totalMachines == 0) {
            return lastSevenDays.stream()
                    .map(day -> new BreakdownPerformanceIndicatorDTO(day, 100.0))
                    .collect(Collectors.toList());
        }

        return lastSevenDays.stream()
                .map(day -> {
                    double downtime = calculateDowntimeForDay(day);
                    double performance = calculatePerformance(downtime, totalMachines);
                    return new BreakdownPerformanceIndicatorDTO(day, performance);
                })
                .collect(Collectors.toList());
    }

    private List<LocalDate> getLastSevenDays() {
        LocalDate today = LocalDate.now();
        return IntStream.range(1, 8)
                .mapToObj(today::minusDays)
                .collect(Collectors.toList());
    }

    private double calculateDowntimeForDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);

        List<Breakdown> breakdowns = breakdownRepository.findBreakdownsAffectingPeriod(startOfDay, endOfDay);

        long totalDowntimeMinutes = 0;

        for (Breakdown breakdown : breakdowns) {
            LocalDateTime breakdownStart = breakdown.getStartedAt();
            LocalDateTime breakdownFinish = breakdown.getFinishedAt() != null
                    ? breakdown.getFinishedAt()
                    : LocalDateTime.now();

            LocalDateTime effectiveStart = breakdownStart.isAfter(startOfDay) ? breakdownStart : startOfDay;
            LocalDateTime effectiveEnd = breakdownFinish.isBefore(endOfDay) ? breakdownFinish : endOfDay;

            if (effectiveStart.isBefore(effectiveEnd)) {
                long minutes = Duration.between(effectiveStart, effectiveEnd).toMinutes();
                totalDowntimeMinutes += minutes;

                log.debug("Awaria maszyny ID: {} ({}), czas w tym dniu: {} minut",
                        breakdown.getMachine().getId(),
                        breakdown.getMachine().getCode(),
                        minutes);
            }
        }

        log.info("Dzień {} – łączny czas awarii: {} minut", day, totalDowntimeMinutes);
        return (double) totalDowntimeMinutes;
    }

    private double calculatePerformance(double downtimeMinutes, long totalMachines) {
        double totalPossibleMinutes = totalMachines * MINUTES_IN_DAY;
        double performance = 100.0 - (downtimeMinutes * 100.0 / totalPossibleMinutes);

        double rounded = Math.round(performance * 10.0) / 10.0;

        log.info("Wydajność: {}% (maszyny: {}, downtime: {} min)", rounded, totalMachines, downtimeMinutes);

        return Math.max(0.0, rounded);
    }

}
