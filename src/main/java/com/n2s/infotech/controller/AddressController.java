package com.n2s.infotech.controller;

import com.n2s.infotech.dto.AddressDto;
import com.n2s.infotech.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des adresses de livraison
 * Tous les endpoints nécessitent une authentification
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getUserAddresses(@RequestParam Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(
            @Valid @RequestBody AddressDto dto,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(addressService.createAddress(userId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto dto,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(addressService.updateAddress(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }
}

