package com.siddu.gamesense.dto;

public record UserGameReviewDTO(
        double ratings,
        String reviewText,
        String parentAsin,
        String title,
        String features
) {
}
