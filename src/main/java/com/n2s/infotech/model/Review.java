package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer rating; // 1-5

    @Column(columnDefinition = "text")
    private String comment;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    private Boolean verified = false; // verified purchase
}

