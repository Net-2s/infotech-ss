package com.n2s.infotech.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateListingRequest {
    private Long productId;
    private Long sellerProfileId;
    private BigDecimal price;
    private Integer quantity;
    private String conditionNote;
}

