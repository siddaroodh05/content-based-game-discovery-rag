package com.siddu.gamesense.services;


import com.siddu.gamesense.dto.*;
import com.siddu.gamesense.utils.InstructionPrompts;
import com.siddu.gamesense.utils.RetrievalQualityPromptBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LlmService {

    private final HuggingFaceChatService huggingFaceChatService;
    private final RetrievalQualityPromptBuilder retrievalQualityPromptBuilder;
    private final InstructionPrompts  instructionPrompts;

    public LlmService(
            HuggingFaceChatService huggingFaceChatService,
            RetrievalQualityPromptBuilder retrievalQualityPromptBuilder,
            InstructionPrompts instructionPrompts) {

        this.huggingFaceChatService = huggingFaceChatService;
        this.retrievalQualityPromptBuilder = retrievalQualityPromptBuilder;
        this.instructionPrompts = instructionPrompts;
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 30000, multiplier = 2)
    )
    public String parseQuery(
            String reviewAndGames,
            String instructions) {

        long start = System.currentTimeMillis();

        String query = huggingFaceChatService.generateQuery(
                instructions,
                reviewAndGames
        );

        System.out.println(
                "Query LLM time: " +
                        (System.currentTimeMillis() - start) +
                        " ms"
        );


        return query;
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 20000, multiplier = 2),
            listeners = "retrievalQualityRetryListener"
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

        return huggingFaceChatService.generateRetrievalQuality(
                instructions,
                userInput
        );
    }
    public List<String>  filteroutRecommendedGames(List<RecommendedGameDTO> recommendedGames,String query) {
        RetrievedResult input = new RetrievedResult(query,recommendedGames);

        String userInput=retrievalQualityPromptBuilder.buildInputforretrieval(input);
        return  huggingFaceChatService.checkretrievedquality(instructionPrompts.qualitycheckinstructions(),userInput);
    }
}