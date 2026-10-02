package com.siddu.gamesense.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record  RetrievalQuality(
        int goodRetrieval,
        int badRetrieval

) {
}
