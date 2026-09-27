package com.siddu.gamesense.services;


import com.siddu.gamesense.dto.LlmInput;
import com.siddu.gamesense.dto.RetrievalQuality;
import com.siddu.gamesense.dto.RetrievedResult;
import com.siddu.gamesense.dto.TopRetrievals;
import com.siddu.gamesense.utils.RetrievalQualityPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final ChatClient chatClient;
    private  final RetrievalQualityPromptBuilder retrievalQualityPromptBuilder;
    public LlmService( @Qualifier("openAiChatModel")ChatModel chatModel,
                     RetrievalQualityPromptBuilder retrievalQualityPromptBuilder) {
        this.chatClient =ChatClient.builder(chatModel).build();
        this.retrievalQualityPromptBuilder = retrievalQualityPromptBuilder;
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 30000, multiplier = 2)
    )
    public String PraseQuery(String reviewandgames, String instructions){

        return chatClient
                .prompt()
                .system(instructions)
                .user(reviewandgames)
                .call()
                .content();
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 20000, multiplier = 2),
            listeners = "retrievalQualityRetryListener"
    )
    public RetrievalQuality RetrievalQualityJudge(String instructions, RetrievedResult result) {

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
        return chatClient
                .prompt()
                .system(instructions)
                .user(retrievalQualityPromptBuilder.buildInput(input))
                .call()
                .entity(RetrievalQuality.class);
    }
}

