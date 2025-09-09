package com.cmms.lite.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "shift_entries",
        uniqueConstraints = @UniqueConstraint(name = "uq_schedule_date_brigade",
                columnNames = {"schedule_id", "work_date", "brigade_type"})
)
public class ShiftEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ShiftSchedule schedule;

    @NotNull
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "brigade_type", nullable = false, length = 1)
    private BrigadeType brigadeType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false, length = 10)
    private ShiftType shiftType;
}
