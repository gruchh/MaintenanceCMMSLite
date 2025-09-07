package com.cmms.lite.core.repository;

import com.cmms.lite.core.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserUsername(String username);

    @Query("SELECT e FROM Employee e JOIN FETCH e.user JOIN FETCH e.employeeRole JOIN FETCH e.employeeDetails WHERE e.id = :id")
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "SELECT e FROM Employee e JOIN FETCH e.user u JOIN FETCH e.employeeRole er", // <-- TUTAJ BRAKOWAŁO PRZECINKA
            countQuery = "SELECT count(e) FROM Employee e")
    Page<Employee> findAllWithSummary(Pageable pageable);

    @Query(value = "SELECT e FROM Employee e JOIN e.user u JOIN e.employeeRole er " +
            "WHERE lower(u.firstName) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(u.lastName) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(u.username) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(u.email) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(er.name) LIKE lower(concat('%', :keyword, '%'))",
            countQuery = "SELECT count(e) FROM Employee e JOIN e.user u JOIN e.employeeRole er " +
                    "WHERE lower(u.firstName) LIKE lower(concat('%', :keyword, '%')) " +
                    "OR lower(u.lastName) LIKE lower(concat('%', :keyword, '%')) " +
                    "OR lower(u.username) LIKE lower(concat('%', :keyword, '%')) " +
                    "OR lower(u.email) LIKE lower(concat('%', :keyword, '%')) " +
                    "OR lower(er.name) LIKE lower(concat('%', :keyword, '%'))")
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}