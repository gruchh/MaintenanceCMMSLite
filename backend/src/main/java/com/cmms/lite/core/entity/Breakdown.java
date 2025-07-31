package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "repaired_at")
    private LocalDateTime repairedAt;

    @NotNull(message = "A machine must be assigned to the failure.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @OneToMany(mappedBy = "failure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FailurePart> usedParts = new ArrayList<>();

    @Column(name = "total_cost", precision = 19, scale = 4)
    private BigDecimal totalCost;

    @PrePersist
    @PreUpdate
    public void calculateTotalCost() {
        if (this.usedParts == null) {
            this.totalCost = BigDecimal.ZERO;
            return;
        }
        this.totalCost = usedParts.stream()
                .map(FailurePart::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}