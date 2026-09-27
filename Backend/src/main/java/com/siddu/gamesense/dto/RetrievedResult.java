package com.siddu.gamesense.dto;

import java.util.List;

public record RetrievedResult(
        String query,
        List<RecommendedGameDTO> games
) {
}
