package com.cmms.lite.api.dto;

import com.cmms.lite.core.entity.BreakdownType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class BreakdownDTOs {

    private BreakdownDTOs() {}

    @Schema(name = "BreakdownCreateRequest", description = "Data required to create a new breakdown report")
    public record CreateRequest(

            @NotBlank String description,
            @NotNull Long machineId,
            @NotNull BreakdownType type
    ) {}

    @Schema(name = "BreakdownAddPartRequest", description = "Data required to add a spare part to a breakdown")
    public record AddPartRequest(
            @Schema(description = "ID of the spare part from the inventory.", example = "105")
            @NotNull @Positive Long sparePartId,

            @Schema(description = "The quantity of parts used.", example = "2")
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

    @Schema(name = "BreakdownUsedPartResponse", description = "A response object for a part used in a breakdown")
    public record UsedPartResponse(
            Long id,
            Long sparePartId,
            String sparePartName,
            Integer quantity,
            BigDecimal pricePerUnit
    ) {}

    @Schema(name = "BreakdownStats", description = "Statistics related to breakdowns")
    public record BreakdownStatsDTO(
            Long daysSinceLastBreakdown,
            Long breakdownsLastWeek,
            Long breakdownsLastMonth,
            Long breakdownsCurrentYear,
            Double averageBreakdownDurationMinutes
    ) {}
}