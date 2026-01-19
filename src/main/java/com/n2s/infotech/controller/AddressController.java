package com.n2s.infotech.controller;

import com.n2s.infotech.dto.AddressDto;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.UserRepository;
import com.n2s.infotech.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;

    private Long getUserIdFromAuth(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<AddressDto>> getUserAddresses(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(
            @Valid @RequestBody AddressDto dto,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(addressService.createAddress(userId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto dto,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(addressService.updateAddress(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuth(authentication);
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<AddressDto> setDefaultAddress(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(addressService.setDefaultAddress(id, userId));
    }
}

