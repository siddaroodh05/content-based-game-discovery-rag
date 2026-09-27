package com.siddu.gamesense.dto;

import java.util.List;

public record LlmInput(
        String query,
        List<TopRetrievals> game
) {
}
