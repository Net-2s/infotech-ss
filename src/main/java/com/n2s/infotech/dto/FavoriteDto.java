package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productBrand;
    private OffsetDateTime addedAt;
}

