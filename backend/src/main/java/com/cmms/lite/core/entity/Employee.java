package com.cmms.lite.core.entity;

import com.cmms.lite.security.entity.User;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Builder.Default
    @ManyToMany(mappedBy = "assignedEmployees")
    private Set<Breakdown> breakdowns = new HashSet<>();
}