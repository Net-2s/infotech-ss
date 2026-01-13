package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "collection_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String distance;

    @ElementCollection
    @CollectionTable(name = "collection_point_materials", joinColumns = @JoinColumn(name = "collection_point_id"))
    @Column(name = "material")
    private List<String> acceptedMaterials;
}

