package com.siddu.gamesense.dto;

public record EvaluatedResult(
        RetrievalQuality TopRatedRetrieval,
        RetrievalQuality  recentBasedRetrieval
) {
}
