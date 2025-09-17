package com.cmms.lite.sparePart.repository;

import com.cmms.lite.sparePart.entity.SparePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    @Query(value = "SELECT s FROM SparePart s " +
            "WHERE lower(s.name) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(s.producer) LIKE lower(concat('%', :keyword, '%'))",
            countQuery = "SELECT count(s) FROM SparePart s " +
                    "WHERE lower(s.name) LIKE lower(concat('%', :keyword, '%')) " +
                    "OR lower(s.producer) LIKE lower(concat('%', :keyword, '%'))")
    Page<SparePart> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
