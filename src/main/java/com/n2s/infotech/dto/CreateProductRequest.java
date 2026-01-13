package com.n2s.infotech.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Le titre est requis")
    private String title;

    private String description;

    @NotBlank(message = "La marque est requise")
    private String brand;

    private String model;

    @NotBlank(message = "La condition est requise")
    private String condition;

    // Optionnel - peut être null
    private Long categoryId;

    // Optional image URLs to upload and associate with product (remote URLs)
    private List<String> imageUrls;

    // Optional passport sub-object (nullable)
    private CreateDigitalPassportRequest passport;
}

