package com.siddu.gamesense.dto;

public record GameMetadataCsvRow (
    String parentAsin,
    String title,
    String categories,
    String features,
    String description,
    Double averageRating,
    Integer ratingNumber,
    String thumb)
{}