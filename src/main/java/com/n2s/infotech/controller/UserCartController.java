package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CartItemDto;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.UserRepository;
import com.n2s.infotech.service.CartService;
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
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
@Tag(name = "User Cart", description = "Gestion du panier de l'utilisateur connecte")
@SecurityRequirement(name = "bearerAuth")
public class UserCartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Recuperer mon panier", description = "Liste tous les articles dans le panier de l'utilisateur connecte")
    public ResponseEntity<List<CartItemDto>> getMyCart(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItemDto> cart = cartService.getUserCart(user.getId());

        return ResponseEntity.ok(cart);
    }

    @PostMapping
    @Operation(summary = "Ajouter au panier", description = "Ajoute un article au panier")
    public ResponseEntity<CartItemDto> addToCart(
            @RequestBody CartItemDto dto,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItemDto cartItem = cartService.addToCart(user.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @PutMapping("/{id}/quantity")
    @Operation(summary = "Modifier la quantite", description = "Modifie la quantite d'un article dans le panier")
    public ResponseEntity<CartItemDto> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItemDto updated = cartService.updateQuantity(id, quantity);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Retirer du panier", description = "Retire un article du panier")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeFromCart(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Vider le panier", description = "Supprime tous les articles du panier")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.clearCart(user.getId());

        return ResponseEntity.noContent().build();
    }
}

