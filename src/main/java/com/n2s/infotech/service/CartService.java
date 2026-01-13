package com.n2s.infotech.service;

import com.n2s.infotech.dto.CartItemDto;
import com.n2s.infotech.model.CartItem;
import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.CartItemRepository;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer le panier d'achats
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    /**
     * Récupère tous les articles du panier d'un utilisateur
     */
    public List<CartItemDto> getUserCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Ajoute un article au panier
     */
    public CartItemDto addToCart(Long userId, CartItemDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Listing listing = listingRepository.findById(dto.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Vérifier la disponibilité
        if (!listing.getActive() || listing.getQuantity() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        // Vérifier si l'article existe déjà dans le panier
        CartItem existingItem = cartItemRepository
                .findByUserIdAndListingId(userId, listing.getId())
                .orElse(null);

        if (existingItem != null) {
            // Mettre à jour la quantité
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();
            if (listing.getQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock");
            }
            existingItem.setQuantity(newQuantity);
            return convertToDto(cartItemRepository.save(existingItem));
        }

        // Créer un nouvel article
        CartItem cartItem = CartItem.builder()
                .user(user)
                .listing(listing)
                .quantity(dto.getQuantity())
                .build();

        return convertToDto(cartItemRepository.save(cartItem));
    }

    /**
     * Met à jour la quantité d'un article du panier
     */
    public CartItemDto updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Vérifier la disponibilité
        if (cartItem.getListing().getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        cartItem.setQuantity(quantity);
        return convertToDto(cartItemRepository.save(cartItem));
    }

    /**
     * Supprime un article du panier
     */
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    /**
     * Vide le panier d'un utilisateur
     */
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    /**
     * Convertit un CartItem en DTO
     */
    private CartItemDto convertToDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .listingId(item.getListing().getId())
                .productTitle(item.getListing().getProduct().getTitle())
                .productBrand(item.getListing().getProduct().getBrand())
                .price(item.getListing().getPrice())
                .quantity(item.getQuantity())
                .sellerShopName(item.getListing().getSeller().getShopName())
                .build();
    }
}

