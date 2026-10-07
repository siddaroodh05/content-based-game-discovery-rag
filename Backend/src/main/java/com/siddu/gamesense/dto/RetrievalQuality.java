package com.siddu.gamesense.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.imageio.ImageWriter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record  RetrievalQuality(
        int goodRetrieval,
        int badRetrieval

) {
}
