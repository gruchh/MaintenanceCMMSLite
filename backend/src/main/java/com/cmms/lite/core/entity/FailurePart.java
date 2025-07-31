package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "failure_parts")
public class FailurePart {

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

    public BigDecimal getCost() {
        if (sparePart != null && sparePart.getPrice() != null) {
            return sparePart.getPrice().multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}
