package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long listingId;
    private String productTitle;
    private String productBrand;
    private BigDecimal price;
    private Integer quantity;
    private String sellerShopName;
}

