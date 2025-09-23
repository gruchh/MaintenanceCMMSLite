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
import java.util.Comparator;
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

        // Sortowanie jest teraz w metodzie getLastSevenDays, więc nie jest tu potrzebne.
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
                    double downtimeMinutes = calculateDowntimeForDay(day);
                    double performance = 100.0 - (downtimeMinutes * 100.0 / (totalMachines * MINUTES_IN_DAY));
                    return new DashboardPerformanceInditatorDTO(day, round(performance, 1));
                })
                .collect(Collectors.toList());
    }

    private DashboardOeeStatsOverallDTO getOeeStatsOverall() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfYear = now.withDayOfYear(1).toLocalDate().atStartOfDay();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        long totalMachines = machineRepository.count();
        if (totalMachines == 0) {
            return new DashboardOeeStatsOverallDTO(100.0, 0.0, 0.0, 0.0, 0.0);
        }

        OeeMetrics yearMetrics = calculateOeeMetricsForPeriod(startOfYear, now, totalMachines);
        OeeMetrics monthMetrics = calculateOeeMetricsForPeriod(startOfMonth, now, totalMachines);

        return new DashboardOeeStatsOverallDTO(
                yearMetrics.availability,
                yearMetrics.mtbf,
                monthMetrics.mtbf,
                yearMetrics.mttr,
                monthMetrics.mttr
        );
    }

    private DashboardInfoAboutUser getDashboardInfoAboutUser() {
        // TODO: Implementacja pobierania danych o zalogowanym użytkowniku
        log.info("Fetching user info...");
        return new DashboardInfoAboutUser("Jan", "Kowalski", 15, "https://i.pravatar.cc/150?u=a042581f4e29026704d", LocalDate.of(2055, 5, 15));
    }

    private DashboardRatingByBreakdownsDTO getEmployeeBreakdownRanking() {
        // TODO: Implementacja rankingu pracowników
        log.info("Generating employee breakdown ranking...");
        List<DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO> workers = List.of(
                new DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO(1L, "Robert Malinowski", "https://i.pravatar.cc/150?u=a042581f4e29026704e", "Technik", 21),
                new DashboardRatingByBreakdownsDTO.WorkerBreakdownDTO(2L, "Ewa Nowak", "https://i.pravatar.cc/150?u=a042581f4e29026704f", "Inżynier Procesu", 18)
        );
        return new DashboardRatingByBreakdownsDTO(workers, "Linia Montażowa A");
    }

    private OeeMetrics calculateOeeMetricsForPeriod(LocalDateTime start, LocalDateTime end, long totalMachines) {
        List<Breakdown> breakdowns = breakdownRepository.findAllByFinishedAtBetween(start, end);

        if (breakdowns.isEmpty()) {
            return new OeeMetrics(100.0, 0.0, 0.0);
        }

        long totalDowntimeSeconds = breakdowns.stream()
                .mapToLong(b -> Duration.between(b.getStartedAt(), b.getFinishedAt()).getSeconds())
                .sum();

        long totalAvailableTimeSeconds = Duration.between(start, end).getSeconds() * totalMachines;
        long totalUptimeSeconds = Math.max(0, totalAvailableTimeSeconds - totalDowntimeSeconds);
        int numberOfBreakdowns = breakdowns.size();

        double mttrHours = (double) totalDowntimeSeconds / numberOfBreakdowns / 3600.0;
        double mtbfHours = (double) totalUptimeSeconds / numberOfBreakdowns / 3600.0;
        double availability = (double) totalUptimeSeconds * 100.0 / totalAvailableTimeSeconds;

        return new OeeMetrics(
                round(availability, 1),
                round(mtbfHours, 1),
                round(mttrHours, 1)
        );
    }

    private double calculateDowntimeForDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);

        List<Breakdown> breakdowns = breakdownRepository.findBreakdownsAffectingPeriod(startOfDay, endOfDay);
        long totalDowntimeMinutes = 0;

        for (Breakdown breakdown : breakdowns) {
            LocalDateTime breakdownStart = breakdown.getStartedAt();
            LocalDateTime breakdownFinish = breakdown.getFinishedAt() != null ? breakdown.getFinishedAt() : LocalDateTime.now();

            LocalDateTime effectiveStart = breakdownStart.isAfter(startOfDay) ? breakdownStart : startOfDay;
            LocalDateTime effectiveEnd = breakdownFinish.isBefore(endOfDay) ? breakdownFinish : endOfDay;

            if (effectiveStart.isBefore(effectiveEnd)) {
                totalDowntimeMinutes += Duration.between(effectiveStart, effectiveEnd).toMinutes();
            }
        }
        return (double) totalDowntimeMinutes;
    }


    private record OeeMetrics(double availability, double mtbf, double mttr) {}

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        return (double) Math.round(value * factor) / factor;
    }
}