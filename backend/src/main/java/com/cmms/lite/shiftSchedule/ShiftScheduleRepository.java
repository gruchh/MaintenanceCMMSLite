package com.cmms.lite.core.repository;

import com.cmms.lite.shiftSchedule.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {
}
