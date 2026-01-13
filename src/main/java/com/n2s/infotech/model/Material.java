package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private Boolean renewable;

    @Column(nullable = false)
    private Boolean recycled;

    @Column(nullable = false)
    private Boolean recyclable;

    private String origin;
}

