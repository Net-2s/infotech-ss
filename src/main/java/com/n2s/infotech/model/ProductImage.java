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

    private String url; // for MVP we'll store path or URL

    private String altText;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}

