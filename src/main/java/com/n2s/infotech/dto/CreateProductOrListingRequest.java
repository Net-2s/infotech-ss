package com.n2s.infotech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateProductOrListingRequest {

    // Product information (required for new products)
    @NotBlank(message = "Le titre est requis")
    private String title;

    @NotBlank(message = "La marque est requise")
    private String brand;

    private String model;

    @NotBlank(message = "La condition est requise")
    private String condition;

    private String description;

    private Long categoryId;

    private List<String> imageUrls;

    private CreateDigitalPassportRequest passport;

    // Listing information (always required)
    @NotNull(message = "Le prix est requis")
    @Positive(message = "Le prix doit être positif")
    private BigDecimal price;

    @NotNull(message = "La quantité est requise")
    @Positive(message = "La quantité doit être positive")
    private Integer quantity;

    private String conditionNote;

    @Builder.Default
    private Boolean active = true;

    // Optional: If user wants to use an existing product
    private Long existingProductId;
}
