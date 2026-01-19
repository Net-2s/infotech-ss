package com.n2s.infotech.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    private BigDecimal total;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "payment_status")
    private String paymentStatus; // pending, succeeded, failed

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress; // JSON string de ShippingAddressDto

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private String status; // CREATED, PAID, SHIPPED, COMPLETED, CANCELLED
}

