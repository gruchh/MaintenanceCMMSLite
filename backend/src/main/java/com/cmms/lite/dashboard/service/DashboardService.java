package com.cmms.lite.dashboard.service;

import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.dashboard.dto.*;
import com.cmms.lite.employee.entity.Employee;
import com.cmms.lite.employee.repository.EmployeeRepository;
import com.cmms.lite.machine.repository.MachineRepository;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.repository.UserRepository;
import com.cmms.lite.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final MachineRepository machineRepository;
    private final BreakdownRepository breakdownRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final OeeCalculator oeeCalculator;

    private static final int MINUTES_IN_DAY = 24 * 60;

    @Transactional(readOnly = true)
    public DashboardSnapshotDTO getDashboardSnapshot() {
        List<DashboardPerformanceInditatorDTO> weeklyPerformance = getWeeklyPerformance();
        DashboardOeeStatsOverallDTO oeeStats = getOeeStatsOverall();
        DashboardInfoAboutUser userInfo = getDashboardInfoAboutUser();
        List<DashboardWorkerBreakdownDTO> ranking = getEmployeeBreakdownRanking();

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

    public DashboardInfoAboutUser getDashboardInfoAboutUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            throw new IllegalStateException("Brak uwierzytelnionego użytkownika");
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Long breakdownCount = breakdownRepository.countByAssignedEmployees_Id(employee.getId());


        return new DashboardInfoAboutUser(
                employee.getFirstName(),
                employee.getLastName(),
                breakdownCount,
                employee.getAvatarUrl(),
                employee.getRetirementDate()
        );
    }

    private List<DashboardWorkerBreakdownDTO> getEmployeeBreakdownRanking() {
        log.info("Generating top 3 employee breakdown ranking...");

        Pageable topThree = PageRequest.of(0, 3);
        return breakdownRepository.findTopEmployeesByBreakdownCount(topThree);
    }
}