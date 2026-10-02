package com.siddu.gamesense.dto;

public record ReviewsResponse (
    String parentAsin,
    String title,
    String thumb,
    double rating,
    String reviewtext
    )
{}
