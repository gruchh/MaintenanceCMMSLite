package com.cmms.lite.core.entity;

import com.cmms.lite.security.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @NotNull(message = "Employee role cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_role_id", nullable = false)
    private EmployeeRole employeeRole;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmployeeDetails employeeDetails;

    @Builder.Default
    @ManyToMany(mappedBy = "assignedEmployees")
    private Set<Breakdown> breakdowns = new HashSet<>();

    @Transient
    public LocalDate getRetirementDate() {
        if (employeeDetails != null) {
            return employeeDetails.getRetirementDate();
        }
        return null;
    }
}