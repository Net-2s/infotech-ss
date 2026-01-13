package com.n2s.infotech.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDigitalPassportRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @NotNull(message = "Carbon footprint is required")
    @Valid
    private CarbonFootprintDto carbonFootprint;
    @NotNull(message = "Traceability is required")
    @Valid
    private TraceabilityDto traceability;
    @NotNull(message = "Materials list is required")
    @Size(min = 1, message = "At least one material is required")
    @Valid
    private List<MaterialDto> materials;
    @NotNull(message = "Durability is required")
    @Valid
    private DurabilityDto durability;
    @Valid
    private List<CertificationDto> certifications;
    @NotNull(message = "Recycling info is required")
    @Valid
    private RecyclingInfoDto recyclingInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CarbonFootprintDto {
        @NotNull @Positive
        private Double totalCO2;
        @NotNull @PositiveOrZero
        private Double manufacturing;
        @NotNull @PositiveOrZero
        private Double transportation;
        @NotNull @PositiveOrZero
        private Double usage;
        @NotNull @PositiveOrZero
        private Double endOfLife;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TraceabilityDto {
        @NotBlank
        private String originCountry;
        @NotBlank
        private String manufacturer;
        private String factory;
        private List<String> supplyChainJourney;
        @NotNull @Min(0) @Max(100)
        private Integer transparencyScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterialDto {
        @NotBlank
        private String name;
        @NotNull @Positive @DecimalMax("100.0")
        private Double percentage;
        @NotNull
        private Boolean renewable;
        @NotNull
        private Boolean recycled;
        @NotNull
        private Boolean recyclable;
        private String origin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DurabilityDto {
        @Positive
        private Integer expectedLifespanYears;
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0")
        private Double repairabilityScore;
        @NotNull
        private Boolean sparePartsAvailable;
        @Positive
        private Integer warrantyYears;
        private Boolean softwareUpdates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertificationDto {
        @NotBlank
        private String name;
        @NotBlank
        private String issuer;
        @NotNull
        private LocalDate validUntil;
        private String logoUrl;
        private String verificationUrl;
        @NotBlank
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecyclingInfoDto {
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
        private Double recyclablePercentage;
        private String instructions;
        @NotNull
        private Boolean takeBackProgram;
        private List<CollectionPointDto> collectionPoints;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CollectionPointDto {
        @NotBlank
        private String name;
        @NotBlank
        private String address;
        @NotBlank
        private String distance;
        private List<String> acceptedMaterials;
    }
}

