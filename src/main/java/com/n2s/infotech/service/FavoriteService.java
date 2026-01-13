package com.n2s.infotech.service;

import com.n2s.infotech.dto.FavoriteDto;
import com.n2s.infotech.model.Favorite;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.FavoriteRepository;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les favoris
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Récupère tous les favoris d'un utilisateur
     */
    public List<FavoriteDto> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Ajoute un produit aux favoris
     */
    public FavoriteDto addFavorite(Long productId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Vérifier si déjà dans les favoris
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        return convertToDto(favoriteRepository.save(favorite));
    }

    /**
     * Retire un produit des favoris
     */
    @Transactional
    public void removeFavorite(Long productId, Long userId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * Vérifie si un produit est dans les favoris
     */
    public boolean isFavorite(Long productId, Long userId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Convertit un Favorite en DTO
     */
    private FavoriteDto convertToDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .productId(favorite.getProduct().getId())
                .productTitle(favorite.getProduct().getTitle())
                .productBrand(favorite.getProduct().getBrand())
                .addedAt(favorite.getAddedAt())
                .build();
    }
}

