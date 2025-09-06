package com.cmms.lite.core.repository;

import com.cmms.lite.core.entity.Breakdown;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BreakdownRepository extends JpaRepository<Breakdown, Long> {

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, b.reportedAt, b.finishedAt)) " +
            "FROM Breakdown b WHERE b.finishedAt IS NOT NULL AND b.reportedAt IS NOT NULL")
    Double getAverageBreakdownDurationInMinutes();

    Long countByFinishedAtBetween(LocalDateTime localDateTime, LocalDateTime now);
    Optional<Breakdown> findTopByOrderByFinishedAtDesc();
    Optional<Breakdown> findTopByOrderByReportedAtDesc();

    @Query("SELECT b FROM Breakdown b JOIN b.machine m " +
            "WHERE lower(b.description) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(m.fullName) LIKE lower(concat('%', :keyword, '%'))")
    Page<Breakdown> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}