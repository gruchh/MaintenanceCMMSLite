package com.cmms.lite.breakdown.dto;

import com.cmms.lite.breakdownType.entity.BreakdownType;
import com.cmms.lite.machine.dto.MachineSummaryDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "BreakdownResponseDTO", description = "Obiekt odpowiedzi zawierający szczegóły awarii")
public record BreakdownResponseDTO(
        @Schema(description = "Unique identifier of the breakdown", example = "1")
        Long id,

        @Schema(description = "Description of the breakdown", example = "Pump failure in production line")
        String description,

        @Schema(description = "When the breakdown was reported", example = "2025-09-10T10:30:00")
        LocalDateTime reportedAt,

        @Schema(description = "When work on the breakdown started", example = "2025-09-10T11:00:00")
        LocalDateTime startedAt,

        @Schema(description = "When the breakdown was finished", example = "2025-09-10T14:30:00")
        LocalDateTime finishedAt,

        @Schema(description = "Whether the breakdown is still open", example = "false")
        Boolean opened,

        @Schema(description = "Comment from the specialist", example = "Replaced faulty pump seal")
        String specialistComment,

        @Schema(description = "Type of breakdown")
        BreakdownType type,

        @Schema(description = "Total cost of repairs", example = "245.50")
        BigDecimal totalCost,

        @Schema(description = "Machine affected by the breakdown")
        MachineSummaryDTO machine,

        @Schema(description = "List of spare parts used during repair")
        List<UsedPartBreakdownDTO> usedParts
) {}