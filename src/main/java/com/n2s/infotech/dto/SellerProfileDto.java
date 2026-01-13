package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfileDto {
    private Long id;
    private String shopName;
    private String description;
    private String contactEmail;
    private Long userId;
    private String userEmail;
}

