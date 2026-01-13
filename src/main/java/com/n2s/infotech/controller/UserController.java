package com.n2s.infotech.controller;

import com.n2s.infotech.dto.ChangePasswordRequest;
import com.n2s.infotech.dto.UpdateProfileRequest;
import com.n2s.infotech.dto.UserProfileDto;
import com.n2s.infotech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour la gestion du profil utilisateur
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Récupérer son propre profil
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer mon profil", description = "Récupère les informations du profil de l'utilisateur connecté")
    public ResponseEntity<UserProfileDto> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        UserProfileDto profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * Modifier son profil
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Modifier mon profil", description = "Modifie les informations du profil")
    public ResponseEntity<UserProfileDto> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserProfileDto updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Changer son mot de passe
     */
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Changer mon mot de passe", description = "Change le mot de passe de l'utilisateur")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }

    /**
     * Supprimer son compte
     */
    @DeleteMapping("/account")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Supprimer mon compte", description = "Supprime définitivement le compte utilisateur")
    public ResponseEntity<Map<String, String>> deleteAccount(Authentication authentication) {
        String email = authentication.getName();
        userService.deleteAccount(email);
        return ResponseEntity.ok(Map.of("message", "Compte supprimé avec succès"));
    }

    /**
     * Récupérer mes statistiques (pour vendeurs)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Mes statistiques de vente", description = "Récupère les statistiques de vente du vendeur")
    public ResponseEntity<Map<String, Object>> getMyStats(Authentication authentication) {
        String email = authentication.getName();
        Map<String, Object> stats = userService.getSellerStats(email);
        return ResponseEntity.ok(stats);
    }
}

