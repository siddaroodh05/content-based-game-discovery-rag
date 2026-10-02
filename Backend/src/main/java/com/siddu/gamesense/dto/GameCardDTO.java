package com.siddu.gamesense.dto;

public record GameCardDTO(
        String parentAsin,
        String title,
        String thumbnail,
        Double averageRating
) {}
