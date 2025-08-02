package com.cmms.lite.api.dto;

import com.cmms.lite.core.entity.BreakdownType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class BreakdownDTOs {

    private BreakdownDTOs() {
    }

    public record CreateRequest(
            @NotBlank String description,
            @NotNull Long machineId,
            @NotNull BreakdownType type
    ) {}

    public record AddPartRequest(
            @NotNull @Positive Long sparePartId,
            @NotNull @Positive Integer quantity
    ) {}

    public record CloseRequest(
            @NotBlank String specialistComment
    ) {}

    public record Response(
            Long id,
            String description,
            LocalDateTime reportedAt,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            Boolean opened,
            String specialistComment,
            BreakdownType type,
            BigDecimal totalCost,
            MachineDTOs.SummaryResponse machine,
            List<UsedPartResponse> usedParts
    ) {}

    public record UsedPartResponse(
            Long id,
            Long sparePartId,
            String sparePartName,
            Integer quantity,
            BigDecimal pricePerUnit
    ) {}

    public record BreakdownStatsDTO(
            Long daysSinceLastBreakdown,
            Long breakdownsLastWeek,
            Long breakdownsLastMonth,
            Long breakdownsCurrentYear,
            Double averageBreakdownDurationMinutes
    ) {}
}