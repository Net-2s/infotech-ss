package com.n2s.infotech.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalPassportDto {
    private Long id;
    private Long productId;
    private CarbonFootprintResponse carbonFootprint;
    private TraceabilityResponse traceability;
    private List<MaterialResponse> materials;
    private DurabilityResponse durability;
    private List<CertificationResponse> certifications;
    private RecyclingInfoResponse recyclingInfo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CarbonFootprintResponse {
        private Long id;
        private Double totalCO2;
        private Double manufacturing;
        private Double transportation;
        private Double usage;
        private Double endOfLife;
        private String score;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TraceabilityResponse {
        private Long id;
        private String originCountry;
        private String manufacturer;
        private String factory;
        private List<String> supplyChainJourney;
        private Integer transparencyScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterialResponse {
        private Long id;
        private String name;
        private Double percentage;
        private Boolean renewable;
        private Boolean recycled;
        private Boolean recyclable;
        private String origin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DurabilityResponse {
        private Long id;
        private Integer expectedLifespanYears;
        private Double repairabilityScore;
        private String repairabilityIndex;
        private Boolean sparePartsAvailable;
        private Integer warrantyYears;
        private Boolean softwareUpdates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertificationResponse {
        private Long id;
        private String name;
        private String issuer;
        private LocalDate validUntil;
        private String logoUrl;
        private String verificationUrl;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecyclingInfoResponse {
        private Long id;
        private Double recyclablePercentage;
        private String instructions;
        private Boolean takeBackProgram;
        private List<CollectionPointResponse> collectionPoints;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CollectionPointResponse {
        private Long id;
        private String name;
        private String address;
        private String distance;
        private List<String> acceptedMaterials;
    }
}

