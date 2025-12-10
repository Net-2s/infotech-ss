package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;

    private String description;

    private String contactEmail;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}

