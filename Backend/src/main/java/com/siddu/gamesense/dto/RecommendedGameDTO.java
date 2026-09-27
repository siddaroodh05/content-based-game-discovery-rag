package com.siddu.gamesense.dto;

public record RecommendedGameDTO(
        String parentAsin,
        String title,
        String categories,
        String features,
        Double averageRating,
        Integer ratingNumber,
        Double distance
) {}
