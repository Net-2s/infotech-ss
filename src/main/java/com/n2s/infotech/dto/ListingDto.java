package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productBrand;
    private List<String> images;
    private Long sellerId;
    private String sellerShopName;
    private BigDecimal price;
    private Integer quantity;
    private String conditionNote;
    private Boolean active;
}

