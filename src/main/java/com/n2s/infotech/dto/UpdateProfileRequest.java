package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String displayName;

    // Info vendeur (optionnel)
    private SellerProfileUpdate sellerProfile;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerProfileUpdate {
        private String shopName;
        private String description;
        private String contactEmail;
    }
}

