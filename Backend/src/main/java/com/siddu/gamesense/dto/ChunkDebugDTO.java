package com.siddu.gamesense.dto;


public record ChunkDebugDTO(
        String parentAsin,
        String title,
        String chunkType,
        String text,
        Double distance
) {}