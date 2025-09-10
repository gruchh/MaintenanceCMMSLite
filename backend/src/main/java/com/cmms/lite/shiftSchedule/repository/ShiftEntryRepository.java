package com.cmms.lite.shiftSchedule.repository;

import com.cmms.lite.shiftSchedule.entity.ShiftEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftEntryRepository extends JpaRepository<ShiftEntry, Long> {
}
