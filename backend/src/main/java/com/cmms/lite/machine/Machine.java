package com.cmms.lite.machine;

import com.cmms.lite.breakdown.entity.Breakdown;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Machine code cannot be blank.")
    @Size(min = 2, max = 50, message = "Machine code must be between 2 and 50 characters.")
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Machine full name cannot be blank.")
    @Size(min = 2, max = 200, message = "Machine full name must be between 2 and 200 characters.")
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Size(max = 255, message = "Serial number cannot be longer than 255 characters.")
    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Size(max = 100, message = "Manufacturer name cannot be longer than 100 characters.")
    @Column(length = 100)
    private String manufacturer;

    @PastOrPresent(message = "Production date cannot be in the future.")
    @Column(name = "production_date")
    private LocalDate productionDate;

    @Lob
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Breakdown> breakdownsList = new ArrayList<>();
}