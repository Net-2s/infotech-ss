package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDto {
    private Double averageRating;
    private Long totalReviews;
    private Map<Integer, Long> ratingCounts; // Distribution des notes 1-5 étoiles
}
