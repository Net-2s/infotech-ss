package com.n2s.infotech.controller;

import com.n2s.infotech.dto.FavoriteDto;
import com.n2s.infotech.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des favoris
 * Tous les endpoints nécessitent une authentification
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getUserFavorites(@RequestParam Long userId) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavoriteDto> addFavorite(
            @PathVariable Long productId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(favoriteService.addFavorite(productId, userId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productId,
            @RequestParam Long userId
    ) {
        favoriteService.removeFavorite(productId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long productId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(favoriteService.isFavorite(productId, userId));
    }
}

