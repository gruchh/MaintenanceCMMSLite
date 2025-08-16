package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "breakdown_used_parts")
public class BreakdownUsedParts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breakdown_id")
    private Breakdown breakdown;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart;

    @Min(value = 1, message = "The quantity of used parts must be greater than 0.")
    @Column(nullable = false)
    private int quantity;
}