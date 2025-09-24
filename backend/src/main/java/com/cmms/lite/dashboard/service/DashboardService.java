package com.cmms.lite.dashboard.service;

import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.dashboard.dto.*;
import com.cmms.lite.machine.repository.MachineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final MachineRepository machineRepository;
    private final BreakdownRepository breakdownRepository;
    private final OeeCalculator oeeCalculator;

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

    @Transactional(readOnly = true)
    public DashboardFactoryStatsDTO getDashboardFactoryStats() {
        LocalDateTime now = LocalDateTime.now();

        Long daysSinceLast = breakdownRepository.findTopByOrderByFinishedAtDesc()
                .map(b -> ChronoUnit.DAYS.between(b.getFinishedAt(), now))
                .orElse(null);

        Long breakdownsLastWeek = breakdownRepository.countByFinishedAtBetween(now.minusWeeks(1), now);
        Long breakdownsLastMonth = breakdownRepository.countByFinishedAtBetween(now.minusMonths(1), now);
        Long breakdownsCurrentYear = breakdownRepository.countByFinishedAtBetween(now.withDayOfYear(1), now);
        Double avgDuration = breakdownRepository.getAverageBreakdownDurationInMinutes();

        List<DashboardPerformanceInditatorDTO> weeklyPerformance = getWeeklyPerformance();
        Double averageEfficiency = weeklyPerformance.stream()
                .mapToDouble(DashboardPerformanceInditatorDTO::performance)
                .average()
                .orElse(100.0);

        return new DashboardFactoryStatsDTO(
                daysSinceLast,
                breakdownsLastWeek,
                breakdownsLastMonth,
                breakdownsCurrentYear,
                avgDuration,
                averageEfficiency
        );
    }

    private List<DashboardPerformanceInditatorDTO> getWeeklyPerformance() {
        long totalMachines = machineRepository.count();
        List<LocalDate> lastSevenDays = oeeCalculator.getLastSevenDays();

        if (totalMachines == 0) {
            log.warn("Brak maszyn w systemie. Zwracam 100% wydajności dla wszystkich dni.");
            return lastSevenDays.stream()
                    .map(day -> new DashboardPerformanceInditatorDTO(day, 100.0))
                    .collect(Collectors.toList());
        }

        return lastSevenDays.stream()
                .map(day -> {
                    double downtimeMinutes = oeeCalculator.calculateDowntimeForDay(day);
                    double performance = 100.0 - (downtimeMinutes * 100.0 / (totalMachines * MINUTES_IN_DAY));
                    return new DashboardPerformanceInditatorDTO(day, Math.round(performance * 10.0) / 10.0);
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

        OeeCalculator.OeeMetrics yearMetrics = oeeCalculator.calculateMetricsForPeriod(startOfYear, now, totalMachines);
        OeeCalculator.OeeMetrics monthMetrics = oeeCalculator.calculateMetricsForPeriod(startOfMonth, now, totalMachines);

        return new DashboardOeeStatsOverallDTO(
                yearMetrics.availability(),
                yearMetrics.mtbf(),
                monthMetrics.mtbf(),
                yearMetrics.mttr(),
                monthMetrics.mttr()
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
}