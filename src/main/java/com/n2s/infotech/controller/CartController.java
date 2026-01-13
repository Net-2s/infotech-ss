package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CartItemDto;
import com.n2s.infotech.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion du panier
 * Tous les endpoints nécessitent une authentification
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    @PostMapping
    public ResponseEntity<CartItemDto> addToCart(
            @RequestBody CartItemDto dto,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(cartService.addToCart(userId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemDto> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(cartService.updateQuantity(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}

