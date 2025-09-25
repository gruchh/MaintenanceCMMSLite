package com.cmms.lite.breakdown.repository;

import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.dashboard.dto.DashboardWorkerBreakdownDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BreakdownRepository extends JpaRepository<Breakdown, Long> {

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, b.reportedAt, b.finishedAt)) " +
            "FROM Breakdown b WHERE b.finishedAt IS NOT NULL AND b.reportedAt IS NOT NULL")
    Double getAverageBreakdownDurationInMinutes();

    Long countByFinishedAtBetween(LocalDateTime localDateTime, LocalDateTime now);
    Optional<Breakdown> findTopByOrderByFinishedAtDesc();
    Optional<Breakdown> findTopByOrderByReportedAtDesc();

    @Query(value = "SELECT b FROM Breakdown b JOIN b.machine m " +
            "WHERE lower(b.description) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(m.fullName) LIKE lower(concat('%', :keyword, '%'))",
            countQuery = "SELECT count(b) FROM Breakdown b JOIN b.machine m " +
                    "WHERE lower(b.description) LIKE lower(concat('%', :keyword, '%')) " +
                    "OR lower(m.fullName) LIKE lower(concat('%', :keyword, '%'))")
    Page<Breakdown> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Breakdown b WHERE b.startedAt <= :end AND (b.finishedAt IS NULL OR b.finishedAt >= :start)")
    List<Breakdown> findBreakdownsAffectingPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Breakdown> findAllByFinishedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByAssignedEmployees_Id(Long employeeId);

    @Query("SELECT new com.cmms.lite.dashboard.dto.DashboardWorkerBreakdownDTO(" +
            "  e.id, " +
            "  e.lastName, " +
            "  e.firstName, " +
            "  er.name, " + // Pobieranie nazwy roli
            "  ed.brigade, " + // Pobieranie brygady
            "  CAST(COUNT(b) AS int)) " + // Rzutowanie wyniku COUNT na int
            "FROM Breakdown b " +
            "JOIN b.assignedEmployees e " +
            "JOIN e.employeeRole er " +
            "JOIN e.employeeDetails ed " + // DODANO JOIN na EmployeeDetails
            "GROUP BY e.id, e.lastName, e.firstName, er.name, ed.brigade " +
            "ORDER BY COUNT(b) DESC")
    List<DashboardWorkerBreakdownDTO> findTopEmployeesByBreakdownCount(Pageable pageable);
}