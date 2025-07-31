package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "sparePart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FailurePart> failureParts;
}