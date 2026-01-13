package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carbon_footprints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonFootprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double totalCO2;

    @Column(nullable = false)
    private Double manufacturing;

    @Column(nullable = false)
    private Double transportation;

    @Column(nullable = false)
    private Double usage;

    @Column(nullable = false)
    private Double endOfLife;

    @Column(nullable = false, length = 1)
    private String score;
}

