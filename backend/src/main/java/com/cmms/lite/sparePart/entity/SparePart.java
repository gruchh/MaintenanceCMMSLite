package com.cmms.lite.sparePart;

import com.cmms.lite.breakdown.entity.BreakdownUsedParts;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spare_parts")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Part name cannot be blank.")
    @Size(max = 150, message = "Part name cannot be longer than 150 characters.")
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Part price cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    @Column(nullable = false)
    private BigDecimal price;

    @Size(max = 150, message = "Producer name cannot be longer than 150 characters.")
    @Column(name = "producer")
    private String producer;

    @OneToMany(mappedBy = "sparePart")
    private List<BreakdownUsedParts> breakdownUsedPartsList;
}