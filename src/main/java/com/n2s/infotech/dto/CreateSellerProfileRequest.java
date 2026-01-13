package com.n2s.infotech.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSellerProfileRequest {

    @NotBlank(message = "Le nom de la boutique est requis")
    private String shopName;

    private String description;

    @NotBlank(message = "L'email de contact est requis")
    @Email(message = "L'email doit être valide")
    private String contactEmail;
}

