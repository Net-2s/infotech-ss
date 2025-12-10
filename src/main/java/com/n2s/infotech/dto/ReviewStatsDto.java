package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDto {
    private Double averageRating;
    private Long totalReviews;
}
