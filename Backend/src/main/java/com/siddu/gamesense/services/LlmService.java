package com.siddu.gamesense.services;


import com.siddu.gamesense.dto.*;
import com.siddu.gamesense.utils.InstructionPrompts;
import com.siddu.gamesense.utils.RetrievalQualityPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
public class LlmService {


    private final RetrievalQualityPromptBuilder retrievalQualityPromptBuilder;
    private final InstructionPrompts  instructionPrompts;
    private  final ChatClient chatClient;


    public LlmService(

            RetrievalQualityPromptBuilder retrievalQualityPromptBuilder,
            InstructionPrompts instructionPrompts,
            @Qualifier("openAiChatModel") ChatModel chatModel) {

        this.retrievalQualityPromptBuilder = retrievalQualityPromptBuilder;
        this.instructionPrompts = instructionPrompts;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultOptions(OpenAiChatOptions.builder().maxTokens(2000))
                .build();
    }

    public String parseQuery(
            String reviewAndGames,
            String instructions) {

        return chatClient.prompt()
                .system(instructions)
                .user(reviewAndGames + "\n/no_think")
                .call()
                .content();

    }


    @Retryable(
            retryFor = RuntimeException.class,
            noRetryFor = NonTransientAiException.class,   // 4xx, including 429, bad key, invalid request
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public RetrievalQuality RetrievalQualityJudge(
            String instructions,
            RetrievedResult result) {

        LlmInput input = new LlmInput(
                result.query(),
                result.games().stream()
                        .map(game -> new TopRetrievals(
                                game.title(),
                                game.categories(),
                                game.features(),
                                game.distance()
                        ))
                        .toList()
        );

        String userInput =
                retrievalQualityPromptBuilder.buildInput(input);

        return chatClient.prompt()
                .system(instructions)
                .user(userInput+ "\n/no_think")
                .call()
                .entity(RetrievalQuality.class);


    }

    public List<String>  filterOutRecommendedGames(List<RecommendedGameDTO> recommendedGames,String query) {
        RetrievedResult input = new RetrievedResult(query,recommendedGames);

        String userInput=retrievalQualityPromptBuilder.buildInputforretrieval(input);

        String parentasins= chatClient.prompt()
                .system(instructionPrompts.qualitycheckinstructions())
                .user(userInput + "\n/no_think")
                .call()
                .content();

        List<String> parentAsins;

        if (parentasins == null || parentasins.trim().equalsIgnoreCase("null")) {
            parentAsins = Collections.emptyList();
        } else {
            parentAsins = Arrays.stream(parentasins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return parentAsins;


    }
}