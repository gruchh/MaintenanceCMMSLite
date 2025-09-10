package com.cmms.lite.sparePart.repository;

import com.cmms.lite.sparePart.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {
}
