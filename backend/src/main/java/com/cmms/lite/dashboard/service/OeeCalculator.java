package com.cmms.lite.dashboard.service;

import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class OeeCalculator {

    private final BreakdownRepository breakdownRepository;

    public OeeMetrics calculateMetricsForPeriod(LocalDateTime start, LocalDateTime end, long totalMachines) {
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

    public double calculateDowntimeForDay(LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(LocalTime.MAX);

        List<Breakdown> breakdowns = breakdownRepository.findBreakdownsAffectingPeriod(startOfDay, endOfDay);

        return breakdowns.stream()
                .mapToLong(b -> {
                    LocalDateTime effectiveStart = b.getStartedAt().isAfter(startOfDay) ? b.getStartedAt() : startOfDay;
                    LocalDateTime effectiveEnd = (b.getFinishedAt() != null && b.getFinishedAt().isBefore(endOfDay)) ? b.getFinishedAt() : endOfDay;
                    return effectiveStart.isBefore(effectiveEnd) ? Duration.between(effectiveStart, effectiveEnd).toMinutes() : 0;
                })
                .sum();
    }

    public List<LocalDate> getLastSevenDays() {
        LocalDate today = LocalDate.now();
        return IntStream.rangeClosed(1, 7)
                .mapToObj(i -> today.minusDays(8L - i))
                .toList();
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        return (double) Math.round(value * factor) / factor;
    }

    public record OeeMetrics(double availability, double mtbf, double mttr) {}
}