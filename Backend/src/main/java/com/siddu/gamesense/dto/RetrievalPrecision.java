package com.siddu.gamesense.dto;

public record RetrievalPrecision(
        double topRatedPrecision,
        double recentBasedPrecision,
        double overallPrecision
) {
}
