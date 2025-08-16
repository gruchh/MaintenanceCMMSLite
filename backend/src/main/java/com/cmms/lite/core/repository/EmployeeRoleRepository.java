package com.cmms.lite.core.repository;

import com.cmms.lite.core.entity.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, Long> {
    Optional<EmployeeRole> findByName(String name);
}