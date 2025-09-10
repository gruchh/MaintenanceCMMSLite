package com.cmms.lite.breakdown;

import com.cmms.lite.sparePart.SparePart;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


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