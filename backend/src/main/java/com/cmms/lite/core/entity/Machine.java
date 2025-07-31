package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Machine name cannot be blank.")
    @Size(min = 2, max = 100, message = "Machine name must be between 2 and 100 characters.")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255, message = "Serial number cannot be longer than 255 characters.")
    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Breakdown> breakdowns;
}