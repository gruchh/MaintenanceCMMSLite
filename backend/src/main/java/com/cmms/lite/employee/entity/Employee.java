package com.cmms.lite.employee.entity;

import com.cmms.lite.breakdown.entity.Breakdown;
import com.cmms.lite.employeeRole.EmployeeRole;
import com.cmms.lite.security.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Long id;

    @NotBlank(message = "First name cannot be blank.")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "avatar_url")
    private String avatarUrl;

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