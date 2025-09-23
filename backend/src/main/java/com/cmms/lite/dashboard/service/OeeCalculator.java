package com.cmms.lite.dashboard.service;

import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class OeeCalculator {

    public static final int MINUTES_IN_DAY = 24 * 60;

    public OeeMetrics calculateOeeMetrics(LocalDateTime start, LocalDateTime end, long machines, BreakdownRepository breakdownRepository) {
        List<Breakdown> breakdowns = breakdownRepository.findAllByFinishedAtBetween(start, end);
        if (breakdowns.isEmpty()) return new OeeMetrics(100.0, 0, 0);

        long downtimeSec = breakdowns.stream()
                .mapToLong(b -> Duration.between(b.getStartedAt(), b.getFinishedAt()).getSeconds())
                .sum();

        long availableSec = Duration.between(start, end).getSeconds() * machines;
        long uptimeSec = Math.max(0, availableSec - downtimeSec);
        int count = breakdowns.size();

        double mttr = (double) downtimeSec / count / 3600;
        double mtbf = (double) uptimeSec / count / 3600;
        double availability = (double) uptimeSec * 100 / availableSec;

        return new OeeMetrics(round(availability), round(mtbf), round(mttr));
    }

    public double calculateDowntimeForDay(LocalDate day, BreakdownRepository breakdownRepository) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.atTime(LocalTime.MAX);

        return breakdownRepository.findBreakdownsAffectingPeriod(start, end).stream()
                .mapToLong(b -> {
                    LocalDateTime s = b.getStartedAt().isAfter(start) ? b.getStartedAt() : start;
                    LocalDateTime f = b.getFinishedAt() != null && b.getFinishedAt().isBefore(end) ? b.getFinishedAt() : end;
                    return s.isBefore(f) ? Duration.between(s, f).toMinutes() : 0;
                })
                .sum();
    }

    public List<LocalDate> getLastSevenDays() {
        LocalDate today = LocalDate.now();
        return IntStream.rangeClosed(1, 7)
                .mapToObj(today::minusDays)
                .sorted()
                .toList();
    }

    public double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public record OeeMetrics(double availability, double mtbf, double mttr) {}
}
