package com.n2s.infotech.controller;

import com.n2s.infotech.dto.FavoriteDto;
import com.n2s.infotech.model.Favorite;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.FavoriteRepository;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<FavoriteDto> getUserFavorites(@RequestParam Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{productId}")
    public FavoriteDto addFavorite(@PathVariable Long productId, @RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        return toDto(favoriteRepository.save(favorite));
    }

    @DeleteMapping("/{productId}")
    @Transactional
    public void removeFavorite(@PathVariable Long productId, @RequestParam Long userId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @GetMapping("/check/{productId}")
    public boolean isFavorite(@PathVariable Long productId, @RequestParam Long userId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    private FavoriteDto toDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .productId(favorite.getProduct().getId())
                .productTitle(favorite.getProduct().getTitle())
                .productBrand(favorite.getProduct().getBrand())
                .addedAt(favorite.getAddedAt())
                .build();
    }
}

