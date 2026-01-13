package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateDigitalPassportRequest;
import com.n2s.infotech.dto.DigitalPassportDto;
import com.n2s.infotech.service.DigitalPassportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/digital-passports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Digital Passports", description = "Gestion des passeports numeriques des produits")
public class DigitalPassportController {

    private final DigitalPassportService digitalPassportService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Recuperer le passeport numerique d'un produit",
               description = "Accessible publiquement pour consultation")
    public ResponseEntity<DigitalPassportDto> getByProductId(@PathVariable Long productId) {
        log.info("REST request to get digital passport for product: {}", productId);
        DigitalPassportDto passport = digitalPassportService.getByProductId(productId);
        return ResponseEntity.ok(passport);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Creer un passeport numerique",
               description = "Reserve aux vendeurs et administrateurs")
    public ResponseEntity<DigitalPassportDto> create(@Valid @RequestBody CreateDigitalPassportRequest request) {
        log.info("REST request to create digital passport for product: {}", request.getProductId());
        DigitalPassportDto created = digitalPassportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mettre a jour un passeport numerique",
               description = "Reserve aux vendeurs et administrateurs")
    public ResponseEntity<DigitalPassportDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateDigitalPassportRequest request) {
        log.info("REST request to update digital passport: {}", id);
        DigitalPassportDto updated = digitalPassportService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Supprimer un passeport numerique",
               description = "Reserve aux administrateurs uniquement")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("REST request to delete digital passport: {}", id);
        digitalPassportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

