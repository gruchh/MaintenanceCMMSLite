package com.cmms.lite.shiftSchedule.repository;

import com.cmms.lite.shiftSchedule.entity.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {
}
