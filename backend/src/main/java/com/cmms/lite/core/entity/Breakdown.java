package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "breakdowns")
public class Breakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Failure description cannot be blank.")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "The report date cannot be null.")
    @PastOrPresent(message = "The report date cannot be in the future.")
    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @PastOrPresent(message = "The start date cannot be in the future.")
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @PastOrPresent(message = "The finish date cannot be in the future.")
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @NotNull(message = "A machine must be assigned to the failure.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @Builder.Default
    @OneToMany(mappedBy = "breakdown")
    private List<BreakdownUsedParts> usedPartsList = new ArrayList<>();

    @Column(name = "total_cost", precision = 19, scale = 4)
    private BigDecimal totalCost;

    @Column(nullable = false)
    private Boolean opened;

    @Column(name = "specialist_comment", length = 2000)
    private String specialistComment;

    @NotNull(message = "Breakdown type cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "breakdown_type", nullable = false)
    private BreakdownType type;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "breakdowns_employees",
            joinColumns = @JoinColumn(name = "breakdown_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> assignedEmployees = new HashSet<>();
}