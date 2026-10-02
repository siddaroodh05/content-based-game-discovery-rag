package com.siddu.gamesense.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siddu.gamesense.Config.HuggingFaceProperties;
import com.siddu.gamesense.dto.HuggingFaceResponse;
import com.siddu.gamesense.dto.RetrievalQuality;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceChatService {

    private final RestClient restClient;
    private final HuggingFaceProperties properties;
    private final ObjectMapper objectMapper;

    public HuggingFaceChatService(
            RestClient.Builder restClientBuilder,
            HuggingFaceProperties properties,
            ObjectMapper objectMapper) {

        this.properties = properties;
        this.objectMapper = objectMapper;

        this.restClient = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + properties.getApiKey()
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    private Map<String, Object> buildRequestBody(
            String instructions,
            String userInput) {

        return Map.of(
                "model", properties.getChat().getModel(),

                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", instructions
                        ),
                        Map.of(
                                "role", "user",
                                "content", userInput
                        )
                ),

                "temperature", properties.getChat().getTemperature(),
                "max_tokens", properties.getChat().getMaxTokens(),

                "chat_template_kwargs", Map.of(
                        "enable_thinking",
                        properties.getChat().isEnableThinking()
                )
        );
    }

    private HuggingFaceResponse sendRequest(
            String instructions,
            String userInput) {

        Map<String, Object> body =
                buildRequestBody(instructions, userInput);

        HuggingFaceResponse response= restClient
                .post()
                .uri("/chat/completions")
                .body(body)
                .retrieve()
                .body(HuggingFaceResponse.class);


        return response;
    }

    public String generateQuery(
            String instructions,
            String reviewAndGames) {

        HuggingFaceResponse response =
                sendRequest(instructions, reviewAndGames);

        return response.choices()
                .get(0)
                .message()
                .content();
    }

    public RetrievalQuality generateRetrievalQuality(
            String instructions,
            String input) {

        HuggingFaceResponse response =
                sendRequest(instructions, input);

        String content = response.choices()
                .get(0)
                .message()
                .content();



        content = content
                .replace("```json", "")
                .replace("```", "")
                .trim();

        try {
            return objectMapper.readValue(
                    content,
                    RetrievalQuality.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to parse RetrievalQuality response: " + content,
                    e
            );
        }
    }

    public List<String>  checkretrievedquality(String instructions,String input){
        HuggingFaceResponse response =
                sendRequest(instructions, input);


        String content = response.choices()
                .get(0)
                .message()
                .content();

        List<String> parentAsins;

        if (content == null || content.trim().equalsIgnoreCase("null")) {
            parentAsins = Collections.emptyList();
        } else {
            parentAsins = Arrays.stream(content.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return parentAsins;

    }
}