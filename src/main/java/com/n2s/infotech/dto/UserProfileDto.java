package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Long id;
    private String email;
    private String displayName;
    private Set<String> roles;
    private OffsetDateTime createdAt;
    private Boolean enabled;

    // Info vendeur (si applicable)
    private SellerInfo sellerInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SellerInfo {
        private Long sellerId;
        private String shopName;
        private String description;
        private String contactEmail;
    }
}

