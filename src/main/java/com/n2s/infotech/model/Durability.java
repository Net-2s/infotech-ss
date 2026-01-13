package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "durability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Durability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer expectedLifespanYears;

    @Column(nullable = false)
    private Double repairabilityScore;

    @Column(nullable = false, length = 1)
    private String repairabilityIndex;

    @Column(nullable = false)
    private Boolean sparePartsAvailable;

    private Integer warrantyYears;

    private Boolean softwareUpdates;
}

