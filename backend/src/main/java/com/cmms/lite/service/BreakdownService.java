package com.cmms.lite.service;

import com.cmms.lite.api.dto.BreakdownDTOs;
import com.cmms.lite.core.entity.Breakdown;
import com.cmms.lite.core.entity.BreakdownUsedParts;
import com.cmms.lite.core.entity.Machine;
import com.cmms.lite.core.entity.SparePart;
import com.cmms.lite.core.mapper.BreakdownMapper;
import com.cmms.lite.core.repository.BreakdownRepository;
import com.cmms.lite.core.repository.MachineRepository;
import com.cmms.lite.core.repository.SparePartRepository;
import com.cmms.lite.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
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

    @Transactional
    public BreakdownDTOs.Response createBreakdown(BreakdownDTOs.CreateRequest request) {
        Machine machine = machineRepository.findById(request.machineId())
                .orElseThrow(() -> new MachineNotFoundException(String.format(MACHINE_NOT_FOUND, request.machineId())));

        Breakdown breakdown = new Breakdown();
        breakdown.setDescription(request.description());
        breakdown.setType(request.type());
        breakdown.setMachine(machine);
        breakdown.setReportedAt(LocalDateTime.now());
        breakdown.setOpened(true);
        breakdown.setTotalCost(BigDecimal.ZERO);

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional(readOnly = true)
    public BreakdownDTOs.Response getBreakdownById(Long id) {
        Breakdown breakdown = getBreakdownByIdOrThrow(id);
        return breakdownMapper.toResponse(breakdown);
    }

    @Transactional(readOnly = true)
    public Page<BreakdownDTOs.Response> getAllBreakdowns(Pageable pageable) {
        return breakdownRepository.findAll(pageable).map(breakdownMapper::toResponse);
    }

    @Transactional
    public BreakdownDTOs.Response addPartToBreakdown(Long breakdownId, BreakdownDTOs.AddPartRequest request) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);
        SparePart sparePart = sparePartRepository.findById(request.sparePartId())
                .orElseThrow(() -> new SparePartNotFoundException(String.format(PART_NOT_FOUND, request.sparePartId())));

        BreakdownUsedParts usedPart = new BreakdownUsedParts();
        usedPart.setBreakdown(breakdown);
        usedPart.setSparePart(sparePart);
        usedPart.setQuantity(request.quantity());

        breakdown.getUsedPartsList().add(usedPart);
        recalculateTotalCost(breakdown);

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional
    public BreakdownDTOs.Response removePartFromBreakdown(Long breakdownId, Long usedPartId) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);
        boolean removed = breakdown.getUsedPartsList().removeIf(part -> part.getId().equals(usedPartId));

        if (!removed) {
            throw new UsedPartNotFoundException("Użyta część o ID " + usedPartId + " nie została znaleziona w awarii o ID " + breakdownId);
        }

        recalculateTotalCost(breakdown);
        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional
    public BreakdownDTOs.Response closeBreakdown(Long breakdownId, BreakdownDTOs.CloseRequest request) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);

        if (!breakdown.getOpened()) {
            throw new IllegalOperationException("Awaria jest już zamknięta.");
        }

        breakdown.setOpened(false);
        breakdown.setFinishedAt(LocalDateTime.now());
        breakdown.setSpecialistComment(request.specialistComment());

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional(readOnly = true)
    public BreakdownDTOs.Response getLatestBreakdown() {
        Breakdown latestBreakdown = breakdownRepository.findTopByOrderByReportedAtDesc()
                .orElseThrow(() -> new BreakdownNotFoundException(LATEST_BREAKDOWN_NOT_FOUND));
        return breakdownMapper.toResponse(latestBreakdown);
    }

    @Transactional(readOnly = true)
    public BreakdownDTOs.BreakdownStatsDTO getBreakdownStats() {
        LocalDateTime now = LocalDateTime.now();
        Long daysSinceLast = breakdownRepository.findTopByOrderByFinishedAtDesc()
                .map(b -> ChronoUnit.DAYS.between(b.getFinishedAt(), now))
                .orElse(null);

        return new BreakdownDTOs.BreakdownStatsDTO(
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
}