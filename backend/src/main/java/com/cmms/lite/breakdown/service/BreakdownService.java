package com.cmms.lite.breakdown.service;

import com.cmms.lite.breakdown.dto.AddPartBreakdownDTO;
import com.cmms.lite.breakdown.dto.BreakdownResponseDTO;
import com.cmms.lite.breakdown.dto.CloseBreakdownDTO;
import com.cmms.lite.breakdown.dto.CreateBreakdownDTO;
import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
import com.cmms.lite.breakdown.exception.BreakdownAlreadyClosedException;
import com.cmms.lite.breakdown.exception.BreakdownNotFoundException;
import com.cmms.lite.breakdown.mapper.BreakdownMapper;
import com.cmms.lite.breakdown.repository.BreakdownRepository;
import com.cmms.lite.machine.entity.Machine;
import com.cmms.lite.machine.exception.MachineNotFoundException;
import com.cmms.lite.machine.repository.MachineRepository;
import com.cmms.lite.sparePart.entity.SparePart;
import com.cmms.lite.sparePart.exception.PartUnavailableException;
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
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BreakdownService {

    private final BreakdownRepository breakdownRepository;
    private final SparePartRepository sparePartRepository;
    private final MachineRepository machineRepository;
    private final BreakdownMapper breakdownMapper;

    private static final String BREAKDOWN_NOT_FOUND = "Breakdown with ID %d was not found.";
    private static final String LATEST_BREAKDOWN_NOT_FOUND = "No breakdowns found in the system.";
    private static final String MACHINE_NOT_FOUND = "Machine with ID %d was not found.";
    private static final String PART_NOT_FOUND = "Spare part with ID %d was not found.";
    private static final String BREAKDOWN_ALREADY_CLOSED = "Operation failed: Breakdown with ID %d is already closed.";

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
                .orElseThrow(() -> new SparePartNotFoundException(
                        String.format(PART_NOT_FOUND, request.getSparePartId())));

        if (sparePart.getStockQuantity() < request.getQuantity()) {
            throw new PartUnavailableException(
                    "Insufficient part number %s. Available: %d, requested: %d."
                            .formatted(sparePart.getName(), sparePart.getStockQuantity(), request.getQuantity()));
        }

        sparePart.setStockQuantity(sparePart.getStockQuantity() - request.getQuantity());
        sparePartRepository.save(sparePart);

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

        BreakdownUsedParts partToRemove = breakdown.getUsedPartsList().stream()
                .filter(part -> part.getId().equals(usedPartId))
                .findFirst()
                .orElseThrow(() -> new UsedPartNotFoundException(
                        "Used part with ID " + usedPartId + " not found in breakdown ID " + breakdownId));

        SparePart sparePart = partToRemove.getSparePart();
        sparePart.setStockQuantity(sparePart.getStockQuantity() + partToRemove.getQuantity());
        sparePartRepository.save(sparePart);

        breakdown.getUsedPartsList().remove(partToRemove);
        recalculateTotalCost(breakdown);

        return breakdownMapper.toResponse(breakdownRepository.save(breakdown));
    }

    @Transactional
    public BreakdownResponseDTO closeBreakdown(Long breakdownId, CloseBreakdownDTO request) {
        Breakdown breakdown = getBreakdownByIdOrThrow(breakdownId);

        if (!breakdown.getOpened()) {
            throw new BreakdownAlreadyClosedException(BREAKDOWN_ALREADY_CLOSED);
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