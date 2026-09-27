package com.siddu.gamesense.dto;

import java.util.List;

public record ChannelResult(
        List<String> retrievedAsins,
        int goodRetrieval,
        int badRetrieval,
        long totalLatencyMs

) {
}
