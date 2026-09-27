package com.siddu.gamesense.dto;

import java.util.List;

public record Result(
        List<UserGameReviewDTO> reviews,
        String query,
        List<RecommendedGameDTO> retrievalData
) {
}
