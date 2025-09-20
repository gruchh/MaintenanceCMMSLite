package com.cmms.lite.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DashboardDTP", description = "Dane wymagane do wyświetlenia statystyk na dashboardzie")
public class DashboardDTO {

    @Schema(description = "Data i czas wygenerowania statystyk", example = "2025-09-20T10:30:00")
    private LocalDateTime generatedAt = LocalDateTime.now();

    DashboardFactoryStatsDTO factoryStatsDTO;
    DashboardOeeStatsOverallDTO oeeStatsOverallDTO;
    DashboardInfoAboutUser dashboardInfoAboutUser;
    DashboardRatingByBreakdownsDTO mostEmployeesBreakdownsDTO;


}