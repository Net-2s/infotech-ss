package com.n2s.infotech.controller;

import com.n2s.infotech.dto.FavoriteDto;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.UserRepository;
import com.n2s.infotech.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
@Tag(name = "User Favorites", description = "Gestion des favoris de l'utilisateur connecte")
@SecurityRequirement(name = "bearerAuth")
public class UserFavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Recuperer mes favoris", description = "Liste tous les produits favoris de l'utilisateur connecte")
    public ResponseEntity<List<FavoriteDto>> getMyFavorites(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FavoriteDto> favorites = favoriteService.getUserFavorites(user.getId());

        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Ajouter aux favoris", description = "Ajoute un produit aux favoris")
    public ResponseEntity<FavoriteDto> addFavorite(
            @PathVariable Long productId,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        FavoriteDto favorite = favoriteService.addFavorite(productId, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Retirer des favoris", description = "Retire un produit des favoris")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productId,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        favoriteService.removeFavorite(productId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Verifier si favori", description = "Verifie si un produit est dans les favoris")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long productId,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isFavorite = favoriteService.isFavorite(productId, user.getId());

        return ResponseEntity.ok(isFavorite);
    }
}

