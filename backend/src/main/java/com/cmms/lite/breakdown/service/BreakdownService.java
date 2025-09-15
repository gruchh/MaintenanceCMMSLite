package com.cmms.lite.breakdown.service;

import com.cmms.lite.breakdown.dto.*;
import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
import com.cmms.lite.breakdown.exception.BreakdownNotFoundException;
import com.cmms.lite.breakdown.mapper.BreakdownMapper;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.exception.IllegalOperationException;
import com.cmms.lite.machine.entity.Machine;
import com.cmms.lite.machine.exception.MachineNotFoundException;
import com.cmms.lite.machine.repository.MachineRepository;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.exception.SparePartNotFoundException;
import com.cmms.lite.sparePart.exception.UsedPartNotFoundException;
import com.cmms.lite.sparePart.repository.SparePartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BreakdownService {

    private final BreakdownRepository breakdownRepository;
    private final SparePartRepository sparePartRepository;
    private final MachineRepository machineRepository;
    private final BreakdownMapper breakdownMapper;

    private static final String BREAKDOWN_NOT_FOUND = "Awaria o ID %d nie została znaleziona.";
    private static final String LATEST_BREAKDOWN_NOT_FOUND = "Nie znaleziono żadnych awarii.";
    private static final String MACHINE_NOT_FOUND = "Maszyna o ID %d nie została znaleziona.";
    private static final String PART_NOT_FOUND = "Część zamienna o ID %d nie została znaleziona.";
    private static final int MINUTES_IN_DAY = 24 * 60;

    @Transactional
    public BreakdownResponseDTO createBreakdown(CreateBreakdownDTO request) {
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new MachineNotFoundException(String.format(MACHINE_NOT_FOUND, request.getMachineId())));

        Breakdown breakdown = new Breakdown();
        breakdown.setDescription(request.getDescription());
        breakdown.setType(request.getType());
        breakdown.setMachine(machine);
        breakdown.setReportedAt(LocalDateTime.now());
        breakdown.setOpened(true);
        breakdown.setTotalCost(BigDecimal.ZERO);

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional(readOnly = true)
    public BreakdownResponseDTO getBreakdownById(Long id) {
        Breakdown breakdown = getBreakdownByIdOrThrow(id);
        return breakdownMapper.toResponse(breakdown);
    }

    @Transactional(readOnly = true)
    public Page<BreakdownResponseDTO> getAllBreakdowns(Pageable pageable) {
        return breakdownRepository.findAll(pageable).map(breakdownMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BreakdownResponseDTO> searchBreakdowns(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBreakdowns(pageable);
        }
        return breakdownRepository.searchByKeyword(keyword, pageable)
                .map(breakdownMapper::toResponse);
    }

    @Transactional
    public BreakdownResponseDTO addPartToBreakdown(Long breakdownId, AddPartBreakdownDTO request) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);
        SparePart sparePart = sparePartRepository.findById(request.getSparePartId())
                .orElseThrow(() -> new SparePartNotFoundException(String.format(PART_NOT_FOUND, request.getSparePartId())));

        BreakdownUsedParts usedPart = new BreakdownUsedParts();
        usedPart.setBreakdown(breakdown);
        usedPart.setSparePart(sparePart);
        usedPart.setQuantity(request.getQuantity());

        breakdown.getUsedPartsList().add(usedPart);
        recalculateTotalCost(breakdown);

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional
    public BreakdownResponseDTO removePartFromBreakdown(Long breakdownId, Long usedPartId) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);
        boolean removed = breakdown.getUsedPartsList().removeIf(part -> part.getId().equals(usedPartId));

        if (!removed) {
            throw new UsedPartNotFoundException("Użyta część o ID " + usedPartId + " nie została znaleziona w awarii o ID " + breakdownId);
        }

        recalculateTotalCost(breakdown);
        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional
    public BreakdownResponseDTO closeBreakdown(Long breakdownId, CloseBreakdownDTO request) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);

        if (!breakdown.getOpened()) {
            throw new IllegalOperationException("Awaria jest już zamknięta.");
        }

        breakdown.setOpened(false);
        breakdown.setFinishedAt(LocalDateTime.now());
        breakdown.setSpecialistComment(request.getSpecialistComment());

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional(readOnly = true)
    public BreakdownResponseDTO getLatestBreakdown() {
        Breakdown latestBreakdown = breakdownRepository.findTopByOrderByReportedAtDesc()
                .orElseThrow(() -> new BreakdownNotFoundException(LATEST_BREAKDOWN_NOT_FOUND));
        return breakdownMapper.toResponse(latestBreakdown);
    }

    @Transactional(readOnly = true)
    public BreakdownStatsDTO getBreakdownStats() {
        LocalDateTime now = LocalDateTime.now();
        Long daysSinceLast = breakdownRepository.findTopByOrderByFinishedAtDesc()
                .map(b -> ChronoUnit.DAYS.between(b.getFinishedAt(), now))
                .orElse(null);

        return new BreakdownStatsDTO(
                daysSinceLast,
                breakdownRepository.countByFinishedAtBetween(now.minusWeeks(1), now),
                breakdownRepository.countByFinishedAtBetween(now.minusMonths(1), now),
                breakdownRepository.countByFinishedAtBetween(now.withDayOfYear(1), now),
                breakdownRepository.getAverageBreakdownDurationInMinutes()
        );
    }

    private Breakdown getBreakdownByIdOrThrow(Long id) {
        return breakdownRepository.findById(id)
                .orElseThrow(() -> new BreakdownNotFoundException(String.format(BREAKDOWN_NOT_FOUND, id)));
    }

    private void recalculateTotalCost(Breakdown breakdown) {
        BigDecimal totalCost = breakdown.getUsedPartsList().stream()
                .map(part -> {
                    BigDecimal price = part.getSparePart().getPrice();
                    return price == null ? BigDecimal.ZERO : price.multiply(new BigDecimal(part.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        breakdown.setTotalCost(totalCost);
    }

    @Transactional(readOnly = true)
    public List<BreakdownPerformanceIndicatorDTO> getWeeklyPerformance() {
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