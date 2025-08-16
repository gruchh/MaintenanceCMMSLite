package com.cmms.lite.core.repository;

import com.cmms.lite.core.entity.Employee;
import com.cmms.lite.core.entity.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserUsername(String username);
}