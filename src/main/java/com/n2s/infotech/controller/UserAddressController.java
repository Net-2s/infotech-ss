package com.n2s.infotech.controller;

import com.n2s.infotech.dto.AddressDto;
import com.n2s.infotech.model.Address;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.AddressRepository;
import com.n2s.infotech.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
@Tag(name = "User Addresses", description = "Gestion des adresses de livraison de l'utilisateur connecte")
@SecurityRequirement(name = "bearerAuth")
public class UserAddressController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Recuperer mes adresses", description = "Liste toutes les adresses de livraison de l'utilisateur connecte")
    public ResponseEntity<List<AddressDto>> getMyAddresses(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AddressDto> addresses = addressRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    @Operation(summary = "Ajouter une adresse", description = "Cree une nouvelle adresse de livraison")
    public ResponseEntity<AddressDto> createAddress(
            @Valid @RequestBody AddressDto dto,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Si c'est la premiere adresse ou si isDefault=true, mettre a jour les autres
        if (dto.getIsDefault() != null && dto.getIsDefault()) {
            addressRepository.findByUserId(user.getId())
                    .forEach(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .fullName(dto.getFullName())
                .street(dto.getStreet())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .phone(dto.getPhone())
                .isDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false)
                .build();

        address = addressRepository.save(address);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(address));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une adresse", description = "Modifie une adresse de livraison existante")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto dto,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verifier que l'adresse appartient a l'utilisateur
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Si on definit cette adresse comme defaut, desactiver les autres
        if (dto.getIsDefault() != null && dto.getIsDefault()) {
            addressRepository.findByUserId(user.getId())
                    .forEach(addr -> {
                        if (!addr.getId().equals(id)) {
                            addr.setIsDefault(false);
                            addressRepository.save(addr);
                        }
                    });
        }

        address.setFullName(dto.getFullName());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }

        address = addressRepository.save(address);

        return ResponseEntity.ok(convertToDto(address));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une adresse", description = "Supprime une adresse de livraison")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verifier que l'adresse appartient a l'utilisateur
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        addressRepository.delete(address);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/set-default")
    @Operation(summary = "Definir comme adresse par defaut", description = "Definit une adresse comme adresse de livraison par defaut")
    public ResponseEntity<AddressDto> setDefaultAddress(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Verifier que l'adresse appartient a l'utilisateur
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Desactiver toutes les autres adresses par defaut
        addressRepository.findByUserId(user.getId())
                .forEach(addr -> {
                    addr.setIsDefault(addr.getId().equals(id));
                    addressRepository.save(addr);
                });

        address = addressRepository.findById(id).get();

        return ResponseEntity.ok(convertToDto(address));
    }

    private AddressDto convertToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.getIsDefault())
                .build();
    }
}

