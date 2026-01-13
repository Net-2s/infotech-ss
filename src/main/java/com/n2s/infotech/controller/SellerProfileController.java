package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateSellerProfileRequest;
import com.n2s.infotech.dto.SellerProfileDto;
import com.n2s.infotech.model.SellerProfile;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.SellerProfileRepository;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@Tag(name = "Seller Profile", description = "API pour la gestion du profil vendeur")
@SecurityRequirement(name = "bearerAuth")
public class SellerProfileController {

    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    /**
     * Créer ou récupérer mon profil vendeur
     * Endpoint dédié pour devenir vendeur
     */
    @PostMapping("/profile/create")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Créer ou récupérer mon profil vendeur", description = "Crée un profil vendeur si n'existe pas, sinon retourne le profil existant")
    public ResponseEntity<SellerProfileDto> createProfile(
            @Valid @RequestBody CreateSellerProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si le profil existe déjà
        Optional<SellerProfile> existing = sellerProfileRepository.findByUser(user);
        if (existing.isPresent()) {
            return ResponseEntity.ok(toDto(existing.get()));
        }

        // Créer le nouveau profil
        SellerProfile profile = SellerProfile.builder()
                .user(user)
                .shopName(request.getShopName())
                .description(request.getDescription())
                .contactEmail(request.getContactEmail())
                .build();

        profile = sellerProfileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(profile));
    }

    /**
     * Récupérer mon profil vendeur
     */
    @GetMapping("/profile/me")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Récupérer mon profil vendeur", description = "Récupère le profil vendeur de l'utilisateur connecté")
    public ResponseEntity<SellerProfileDto> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SellerProfile profile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        return ResponseEntity.ok(toDto(profile));
    }

    /**
     * Modifier mon profil vendeur
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Modifier mon profil vendeur", description = "Modifie le profil vendeur de l'utilisateur connecté")
    public ResponseEntity<SellerProfileDto> updateProfile(
            @Valid @RequestBody CreateSellerProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SellerProfile profile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        profile.setShopName(request.getShopName());
        profile.setDescription(request.getDescription());
        profile.setContactEmail(request.getContactEmail());

        profile = sellerProfileRepository.save(profile);

        return ResponseEntity.ok(toDto(profile));
    }

    /**
     * Supprimer mon profil vendeur
     */
    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Supprimer mon profil vendeur", description = "Supprime le profil vendeur de l'utilisateur connecté")
    public ResponseEntity<Void> deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SellerProfile profile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        sellerProfileRepository.delete(profile);

        return ResponseEntity.noContent().build();
    }

    private SellerProfileDto toDto(SellerProfile profile) {
        return SellerProfileDto.builder()
                .id(profile.getId())
                .shopName(profile.getShopName())
                .description(profile.getDescription())
                .contactEmail(profile.getContactEmail())
                .userId(profile.getUser().getId())
                .userEmail(profile.getUser().getEmail())
                .build();
    }
}

