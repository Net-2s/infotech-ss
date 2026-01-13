package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "recycling_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecyclingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double recyclablePercentage;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(nullable = false)
    private Boolean takeBackProgram;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recycling_info_id")
    private List<CollectionPoint> collectionPoints;
}

