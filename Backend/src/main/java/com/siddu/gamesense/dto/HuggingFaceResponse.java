package com.siddu.gamesense.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
public record HuggingFaceResponse(
        List<Choice> choices,
        Usage usage
) {

    public record Choice(
            Message message
    ) {}

    public record Message(
            String content
    ) {}

    public record Usage(
            int prompt_tokens,
            int completion_tokens,
            int total_tokens
    ) {}
}