package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url; // URL complète Cloudinary

    private String publicId; // Public ID Cloudinary pour la suppression

    private String altText;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

