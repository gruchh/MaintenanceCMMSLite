package com.cmms.lite.dashboard.service;

import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.dashboard.dto.*;
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

    private static final int MINUTES_IN_DAY = 24 * 60;

    @Transactional(readOnly = true)
    public DashboardSnapshotDTO getDashboardSnapshot() {
        List<DashboardPerformanceInditatorDTO> weeklyPerformance = getWeeklyPerformance();
        DashboardOeeStatsOverallDTO oeeStats = getOeeStatsOverall();
        DashboardInfoAboutUser userInfo = getDashboardInfoAboutUser();
        DashboardRatingByBreakdownsDTO ranking = getEmployeeBreakdownRanking();
        return new DashboardSnapshotDTO(
                weeklyPerformance,
                oeeStats,
                userInfo,
                ranking
        );
    }

    private List<DashboardPerformanceInditatorDTO> getWeeklyPerformance() {
        long totalMachines = machineRepository.count();
        List<LocalDate> lastSevenDays = getLastSevenDays();

        if (totalMachines == 0) {
            log.warn("Brak maszyn w systemie. Zwracam 100% wydajności dla wszystkich dni.");
            return lastSevenDays.stream()
                    .map(day -> new DashboardPerformanceInditatorDTO(day, 100.0))
                    .collect(Collectors.toList());
        }

        return lastSevenDays.stream()
                .map(day -> {
                    double downtime = calculateDowntimeForDay(day);
                    double performance = calculatePerformance(downtime, totalMachines);
                    return new DashboardPerformanceInditatorDTO(day, performance);
                })
                .collect(Collectors.toList());
    }

    private DashboardOeeStatsOverallDTO getOeeStatsOverall() {
        log.info("Calculating OEE stats...");
        return new DashboardOeeStatsOverallDTO(85.5, 250.0, 22.0, 1.5, 0.9);
    }

    private DashboardInfoAboutUser getDashboardInfoAboutUser() {
        log.info("Fetching user info...");
        return new DashboardInfoAboutUser("Jan", "Kowalski", 15, "https://i.pravatar.cc/150?u=a042581f4e29026704d", LocalDate.of(2055, 5, 15));
    }

    private DashboardRatingByBreakdownsDTO getEmployeeBreakdownRanking() {
        log.info("Generating employee breakdown ranking...");
        List<DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO> workers = List.of(
                new DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO(1L, "Robert Malinowski", "https://i.pravatar.cc/150?u=a042581f4e29026704e", "Technik", 21),
                new DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO(2L, "Ewa Nowak", "https://i.pravatar.cc/150?u=a042581f4e29026704f", "Inżynier Procesu", 18),
                new DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO(3L, "Piotr Zieliński", "https://i.pravatar.cc/150?u=a042581f4e29026704a", "Automatyk", 15)
        );
        return new DashboardRatingByBreakdownsDTO(workers, "Linia Montażowa A");
    }

    private List<LocalDate> getLastSevenDays() {
        LocalDate today = LocalDate.now();
        return IntStream.range(1, 8)
                .map(i -> 8 - i)
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
            }
        }
        log.debug("Dzień {} – łączny czas awarii: {} minut", day, totalDowntimeMinutes);
        return (double) totalDowntimeMinutes;
    }

    private double calculatePerformance(double downtimeMinutes, long totalMachines) {
        double totalPossibleMinutes = totalMachines * MINUTES_IN_DAY;
        double performance = 100.0 - (downtimeMinutes * 100.0 / totalPossibleMinutes);
        double rounded = Math.round(performance * 10.0) / 10.0;
        return Math.max(0.0, rounded);
    }
}