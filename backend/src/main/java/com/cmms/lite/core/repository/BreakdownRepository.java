package com.cmms.lite.core.repository;

import com.cmms.lite.core.entity.Breakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakdownRepository extends JpaRepository<Breakdown, Long> {
}