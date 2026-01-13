package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "traceability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Traceability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originCountry;

    @Column(nullable = false)
    private String manufacturer;

    private String factory;

    @ElementCollection
    @CollectionTable(name = "supply_chain_journey", joinColumns = @JoinColumn(name = "traceability_id"))
    @Column(name = "step")
    private List<String> supplyChainJourney;

    @Column(nullable = false)
    private Integer transparencyScore;
}

