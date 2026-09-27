package com.siddu.gamesense.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siddu.gamesense.dto.LlmInput;
import com.siddu.gamesense.dto.RetrievedResult;
import org.springframework.stereotype.Component;

@Component
public class RetrievalQualityPromptBuilder {

    private final ObjectMapper objectMapper;

    public RetrievalQualityPromptBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildInput(LlmInput result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize retrieval result", e
            );
        }
    }
}