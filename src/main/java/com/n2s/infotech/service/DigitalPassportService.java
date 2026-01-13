package com.n2s.infotech.service;

import com.n2s.infotech.dto.CreateDigitalPassportRequest;
import com.n2s.infotech.dto.DigitalPassportDto;
import com.n2s.infotech.model.*;
import com.n2s.infotech.repository.DigitalPassportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalPassportService {

    private final DigitalPassportRepository digitalPassportRepository;

    @Transactional(readOnly = true)
    public DigitalPassportDto getByProductId(Long productId) {
        log.info("Fetching digital passport for product ID: {}", productId);
        DigitalPassport passport = digitalPassportRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Digital passport not found for product: " + productId));
        return mapToDto(passport);
    }

    @Transactional
    public DigitalPassportDto create(CreateDigitalPassportRequest request) {
        log.info("Creating digital passport for product ID: {}", request.getProductId());

        double totalPercentage = request.getMaterials().stream()
                .mapToDouble(CreateDigitalPassportRequest.MaterialDto::getPercentage)
                .sum();

        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException("Materials percentages must sum to 100%. Current sum: " + totalPercentage + "%");
        }

        if (digitalPassportRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException("Digital passport already exists for product: " + request.getProductId());
        }

        DigitalPassport passport = mapToEntity(request);
        DigitalPassport saved = digitalPassportRepository.save(passport);

        log.info("Digital passport created with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    @Transactional
    public DigitalPassportDto update(Long id, CreateDigitalPassportRequest request) {
        log.info("Updating digital passport ID: {}", id);

        DigitalPassport passport = digitalPassportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Digital passport not found: " + id));

        double totalPercentage = request.getMaterials().stream()
                .mapToDouble(CreateDigitalPassportRequest.MaterialDto::getPercentage)
                .sum();

        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException("Materials percentages must sum to 100%. Current sum: " + totalPercentage + "%");
        }

        updatePassportFromRequest(passport, request);
        DigitalPassport updated = digitalPassportRepository.save(passport);

        log.info("Digital passport updated: {}", id);
        return mapToDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting digital passport ID: {}", id);

        if (!digitalPassportRepository.existsById(id)) {
            throw new RuntimeException("Digital passport not found: " + id);
        }

        digitalPassportRepository.deleteById(id);
        log.info("Digital passport deleted: {}", id);
    }

    private String calculateCarbonScore(Double totalCO2) {
        if (totalCO2 < 10) return "A";
        if (totalCO2 < 25) return "B";
        if (totalCO2 < 50) return "C";
        if (totalCO2 < 100) return "D";
        return "E";
    }

    private String calculateRepairabilityIndex(Double score) {
        if (score >= 8) return "A";
        if (score >= 6) return "B";
        if (score >= 4) return "C";
        if (score >= 2) return "D";
        return "E";
    }

    private DigitalPassport mapToEntity(CreateDigitalPassportRequest request) {
        CarbonFootprint carbonFootprint = CarbonFootprint.builder()
                .totalCO2(request.getCarbonFootprint().getTotalCO2())
                .manufacturing(request.getCarbonFootprint().getManufacturing())
                .transportation(request.getCarbonFootprint().getTransportation())
                .usage(request.getCarbonFootprint().getUsage())
                .endOfLife(request.getCarbonFootprint().getEndOfLife())
                .score(calculateCarbonScore(request.getCarbonFootprint().getTotalCO2()))
                .build();

        Traceability traceability = Traceability.builder()
                .originCountry(request.getTraceability().getOriginCountry())
                .manufacturer(request.getTraceability().getManufacturer())
                .factory(request.getTraceability().getFactory())
                .supplyChainJourney(request.getTraceability().getSupplyChainJourney())
                .transparencyScore(request.getTraceability().getTransparencyScore())
                .build();

        List<Material> materials = request.getMaterials().stream()
                .map(m -> Material.builder()
                        .name(m.getName())
                        .percentage(m.getPercentage())
                        .renewable(m.getRenewable())
                        .recycled(m.getRecycled())
                        .recyclable(m.getRecyclable())
                        .origin(m.getOrigin())
                        .build())
                .collect(Collectors.toList());

        Durability durability = Durability.builder()
                .expectedLifespanYears(request.getDurability().getExpectedLifespanYears())
                .repairabilityScore(request.getDurability().getRepairabilityScore())
                .repairabilityIndex(calculateRepairabilityIndex(request.getDurability().getRepairabilityScore()))
                .sparePartsAvailable(request.getDurability().getSparePartsAvailable())
                .warrantyYears(request.getDurability().getWarrantyYears())
                .softwareUpdates(request.getDurability().getSoftwareUpdates())
                .build();

        List<Certification> certifications = request.getCertifications() != null ?
                request.getCertifications().stream()
                        .map(c -> Certification.builder()
                                .name(c.getName())
                                .issuer(c.getIssuer())
                                .validUntil(c.getValidUntil())
                                .logoUrl(c.getLogoUrl())
                                .verificationUrl(c.getVerificationUrl())
                                .type(c.getType())
                                .build())
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        List<CollectionPoint> collectionPoints = request.getRecyclingInfo().getCollectionPoints() != null ?
                request.getRecyclingInfo().getCollectionPoints().stream()
                        .map(cp -> CollectionPoint.builder()
                                .name(cp.getName())
                                .address(cp.getAddress())
                                .distance(cp.getDistance())
                                .acceptedMaterials(cp.getAcceptedMaterials())
                                .build())
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        RecyclingInfo recyclingInfo = RecyclingInfo.builder()
                .recyclablePercentage(request.getRecyclingInfo().getRecyclablePercentage())
                .instructions(request.getRecyclingInfo().getInstructions())
                .takeBackProgram(request.getRecyclingInfo().getTakeBackProgram())
                .collectionPoints(collectionPoints)
                .build();

        return DigitalPassport.builder()
                .productId(request.getProductId())
                .carbonFootprint(carbonFootprint)
                .traceability(traceability)
                .materials(materials)
                .durability(durability)
                .certifications(certifications)
                .recyclingInfo(recyclingInfo)
                .build();
    }

    private DigitalPassportDto mapToDto(DigitalPassport passport) {
        return DigitalPassportDto.builder()
                .id(passport.getId())
                .productId(passport.getProductId())
                .carbonFootprint(mapCarbonFootprintToDto(passport.getCarbonFootprint()))
                .traceability(mapTraceabilityToDto(passport.getTraceability()))
                .materials(passport.getMaterials().stream()
                        .map(this::mapMaterialToDto)
                        .collect(Collectors.toList()))
                .durability(mapDurabilityToDto(passport.getDurability()))
                .certifications(passport.getCertifications().stream()
                        .map(this::mapCertificationToDto)
                        .collect(Collectors.toList()))
                .recyclingInfo(mapRecyclingInfoToDto(passport.getRecyclingInfo()))
                .createdAt(passport.getCreatedAt())
                .updatedAt(passport.getUpdatedAt())
                .build();
    }

    private DigitalPassportDto.CarbonFootprintResponse mapCarbonFootprintToDto(CarbonFootprint cf) {
        return DigitalPassportDto.CarbonFootprintResponse.builder()
                .id(cf.getId())
                .totalCO2(cf.getTotalCO2())
                .manufacturing(cf.getManufacturing())
                .transportation(cf.getTransportation())
                .usage(cf.getUsage())
                .endOfLife(cf.getEndOfLife())
                .score(cf.getScore())
                .build();
    }

    private DigitalPassportDto.TraceabilityResponse mapTraceabilityToDto(Traceability t) {
        return DigitalPassportDto.TraceabilityResponse.builder()
                .id(t.getId())
                .originCountry(t.getOriginCountry())
                .manufacturer(t.getManufacturer())
                .factory(t.getFactory())
                .supplyChainJourney(t.getSupplyChainJourney())
                .transparencyScore(t.getTransparencyScore())
                .build();
    }

    private DigitalPassportDto.MaterialResponse mapMaterialToDto(Material m) {
        return DigitalPassportDto.MaterialResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .percentage(m.getPercentage())
                .renewable(m.getRenewable())
                .recycled(m.getRecycled())
                .recyclable(m.getRecyclable())
                .origin(m.getOrigin())
                .build();
    }

    private DigitalPassportDto.DurabilityResponse mapDurabilityToDto(Durability d) {
        return DigitalPassportDto.DurabilityResponse.builder()
                .id(d.getId())
                .expectedLifespanYears(d.getExpectedLifespanYears())
                .repairabilityScore(d.getRepairabilityScore())
                .repairabilityIndex(d.getRepairabilityIndex())
                .sparePartsAvailable(d.getSparePartsAvailable())
                .warrantyYears(d.getWarrantyYears())
                .softwareUpdates(d.getSoftwareUpdates())
                .build();
    }

    private DigitalPassportDto.CertificationResponse mapCertificationToDto(Certification c) {
        return DigitalPassportDto.CertificationResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .issuer(c.getIssuer())
                .validUntil(c.getValidUntil())
                .logoUrl(c.getLogoUrl())
                .verificationUrl(c.getVerificationUrl())
                .type(c.getType())
                .build();
    }

    private DigitalPassportDto.RecyclingInfoResponse mapRecyclingInfoToDto(RecyclingInfo ri) {
        List<DigitalPassportDto.CollectionPointResponse> collectionPoints = ri.getCollectionPoints() != null ?
                ri.getCollectionPoints().stream()
                        .map(cp -> DigitalPassportDto.CollectionPointResponse.builder()
                                .id(cp.getId())
                                .name(cp.getName())
                                .address(cp.getAddress())
                                .distance(cp.getDistance())
                                .acceptedMaterials(cp.getAcceptedMaterials())
                                .build())
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        return DigitalPassportDto.RecyclingInfoResponse.builder()
                .id(ri.getId())
                .recyclablePercentage(ri.getRecyclablePercentage())
                .instructions(ri.getInstructions())
                .takeBackProgram(ri.getTakeBackProgram())
                .collectionPoints(collectionPoints)
                .build();
    }

    private void updatePassportFromRequest(DigitalPassport passport, CreateDigitalPassportRequest request) {
        CarbonFootprint cf = passport.getCarbonFootprint();
        cf.setTotalCO2(request.getCarbonFootprint().getTotalCO2());
        cf.setManufacturing(request.getCarbonFootprint().getManufacturing());
        cf.setTransportation(request.getCarbonFootprint().getTransportation());
        cf.setUsage(request.getCarbonFootprint().getUsage());
        cf.setEndOfLife(request.getCarbonFootprint().getEndOfLife());
        cf.setScore(calculateCarbonScore(request.getCarbonFootprint().getTotalCO2()));

        Traceability t = passport.getTraceability();
        t.setOriginCountry(request.getTraceability().getOriginCountry());
        t.setManufacturer(request.getTraceability().getManufacturer());
        t.setFactory(request.getTraceability().getFactory());
        t.setSupplyChainJourney(request.getTraceability().getSupplyChainJourney());
        t.setTransparencyScore(request.getTraceability().getTransparencyScore());

        passport.getMaterials().clear();
        request.getMaterials().forEach(m ->
                passport.getMaterials().add(Material.builder()
                        .name(m.getName())
                        .percentage(m.getPercentage())
                        .renewable(m.getRenewable())
                        .recycled(m.getRecycled())
                        .recyclable(m.getRecyclable())
                        .origin(m.getOrigin())
                        .build()));

        Durability d = passport.getDurability();
        d.setExpectedLifespanYears(request.getDurability().getExpectedLifespanYears());
        d.setRepairabilityScore(request.getDurability().getRepairabilityScore());
        d.setRepairabilityIndex(calculateRepairabilityIndex(request.getDurability().getRepairabilityScore()));
        d.setSparePartsAvailable(request.getDurability().getSparePartsAvailable());
        d.setWarrantyYears(request.getDurability().getWarrantyYears());
        d.setSoftwareUpdates(request.getDurability().getSoftwareUpdates());

        passport.getCertifications().clear();
        if (request.getCertifications() != null) {
            request.getCertifications().forEach(c ->
                    passport.getCertifications().add(Certification.builder()
                            .name(c.getName())
                            .issuer(c.getIssuer())
                            .validUntil(c.getValidUntil())
                            .logoUrl(c.getLogoUrl())
                            .verificationUrl(c.getVerificationUrl())
                            .type(c.getType())
                            .build()));
        }

        RecyclingInfo ri = passport.getRecyclingInfo();
        ri.setRecyclablePercentage(request.getRecyclingInfo().getRecyclablePercentage());
        ri.setInstructions(request.getRecyclingInfo().getInstructions());
        ri.setTakeBackProgram(request.getRecyclingInfo().getTakeBackProgram());

        ri.getCollectionPoints().clear();
        if (request.getRecyclingInfo().getCollectionPoints() != null) {
            request.getRecyclingInfo().getCollectionPoints().forEach(cp ->
                    ri.getCollectionPoints().add(CollectionPoint.builder()
                            .name(cp.getName())
                            .address(cp.getAddress())
                            .distance(cp.getDistance())
                            .acceptedMaterials(cp.getAcceptedMaterials())
                            .build()));
        }
    }
}

