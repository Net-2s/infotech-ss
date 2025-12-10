package com.n2s.infotech.controller;

import com.n2s.infotech.dto.ReviewDto;
import com.n2s.infotech.dto.ReviewStatsDto;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.Review;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.ReviewRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping("/product/{productId}")
    public Page<ReviewDto> getProductReviews(@PathVariable Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::toDto);
    }

    @GetMapping("/product/{productId}/stats")
    public ReviewStatsDto getProductReviewStats(@PathVariable Long productId) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countByProductId(productId);

        return new ReviewStatsDto(
                avgRating != null ? avgRating : 0.0,
                totalReviews
        );
    }

    @PostMapping
    public ReviewDto createReview(@Valid @RequestBody ReviewDto dto) {
        // Le userId doit être dans le DTO
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        return toDto(reviewRepository.save(review));
    }


    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getDisplayName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .verified(review.getVerified())
                .build();
    }
}


