package com.n2s.infotech.service;

import com.n2s.infotech.dto.ReviewDto;
import com.n2s.infotech.dto.ReviewStatsDto;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.Review;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.ReviewRepository;
import com.n2s.infotech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service pour gérer les avis produits
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Récupère tous les avis d'un produit
     */
    public Page<ReviewDto> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::convertToDto);
    }

    /**
     * Récupère les statistiques d'avis d'un produit
     */
    public ReviewStatsDto getProductReviewStats(Long productId) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countByProductId(productId);
        
        // Calculer la distribution des notes (1-5 étoiles)
        java.util.Map<Integer, Long> ratingCounts = new java.util.HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = reviewRepository.countByProductIdAndRating(productId, i);
            ratingCounts.put(i, count != null ? count : 0L);
        }
        
        return new ReviewStatsDto(
                avgRating != null ? avgRating : 0.0,
                totalReviews,
                ratingCounts
        );
    }

    /**
     * Crée un nouvel avis
     */
    public ReviewDto createReview(ReviewDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Vérifier si l'utilisateur a déjà laissé un avis
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(dto.getRating())
                .title(dto.getTitle())
                .comment(dto.getComment())
                .verified(false) // Sera vérifié après achat
                .build();

        return convertToDto(reviewRepository.save(review));
    }

    /**
     * Supprime un avis
     */
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Vérifier que l'utilisateur est bien l'auteur
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    /**
     * Convertit un Review en DTO
     */
    private ReviewDto convertToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getDisplayName())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .verified(review.getVerified())
                .build();
    }
}
