package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CartItemDto;
import com.n2s.infotech.model.CartItem;
import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.CartItemRepository;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<CartItemDto> getCart(@RequestParam Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public CartItemDto addToCart(@RequestBody CartItemDto dto, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Listing listing = listingRepository.findById(dto.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Check if item already in cart
        CartItem existingItem = cartItemRepository.findByUserIdAndListingId(user.getId(), listing.getId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            return toDto(cartItemRepository.save(existingItem));
        }

        CartItem cartItem = CartItem.builder()
                .user(user)
                .listing(listing)
                .quantity(dto.getQuantity())
                .build();

        return toDto(cartItemRepository.save(cartItem));
    }

    @PutMapping("/{id}")
    public CartItemDto updateQuantity(@PathVariable Long id, @RequestBody CartItemDto dto) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItem.setQuantity(dto.getQuantity());
        return toDto(cartItemRepository.save(cartItem));
    }

    @DeleteMapping("/{id}")
    public void removeFromCart(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
    }

    @DeleteMapping
    @Transactional
    public void clearCart(@RequestParam Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItemDto toDto(CartItem item) {
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

