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

    @Schema(name = "BreakdownCreateRequest", description = "Dane wymagane do utworzenia nowego zgłoszenia awarii")
    public record CreateRequest(

            @NotBlank String description,
            @NotNull Long machineId,
            @NotNull BreakdownType type
    ) {}

    @Schema(name = "BreakdownAddPartRequest", description = "Dane wymagane do dodania części zamiennej do awarii")
    public record AddPartRequest(
            @Schema(description = "ID części zamiennej z magazynu.", example = "105")
            @NotNull @Positive Long sparePartId,

            @Schema(description = "Liczba użytych części.", example = "2")
            @NotNull @Positive Integer quantity
    ) {}

    @Schema(name = "BreakdownCloseRequest", description = "Dane wymagane do zamknięcia zgłoszenia awarii")
    public record CloseRequest(
            @NotBlank String specialistComment
    ) {}

    @Schema(name = "BreakdownResponse", description = "Obiekt odpowiedzi zawierający szczegóły awarii")
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

    @Schema(name = "BreakdownUsedPartResponse", description = "Obiekt odpowiedzi dla części użytej podczas awarii")
    public record UsedPartResponse(
            Long id,
            Long sparePartId,
            String sparePartName,
            Integer quantity,
            BigDecimal pricePerUnit
    ) {}

    @Schema(name = "BreakdownStats", description = "Statystyki związane z awariami")
    public record BreakdownStatsDTO(
            Long daysSinceLastBreakdown,
            Long breakdownsLastWeek,
            Long breakdownsLastMonth,
            Long breakdownsCurrentYear,
            Double averageBreakdownDurationMinutes
    ) {}
}