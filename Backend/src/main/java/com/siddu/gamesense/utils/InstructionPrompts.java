package com.siddu.gamesense.utils;

import com.siddu.gamesense.dto.UserGameReviewDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.truncate;

@Component
public class InstructionPrompts {


    public String queryinstructions(){
        return """
                Analyze the user's game reviews, ratings, and game metadata.
                
                Infer the user's actual game preferences.
                
                Use review text and ratings as the primary signals.
                Use the game title and features only to identify the gameplay
                features, genres, themes, and mechanics associated with those preferences.
                
                Positive reviews/ratings indicate things the user enjoys.
                Negative reviews/ratings indicate things the user dislikes or wants to avoid.
                
                Do not assume every feature of a highly-rated game is a preference.
                Do not treat features of a poorly-rated game as preferences.
                
                Generate one concise semantic search query representing the user's
                overall game preferences.
                
                Do not mention specific game titles.
                Do not explain your reasoning.
                Return only the query.
                Keep the query under 50 tokens.
                """;
    }
    public String prasegamereviewdto(List<UserGameReviewDTO> reviewandgames){
        String input= reviewandgames.stream()
                .map(dto -> """
                Rating: %s
                Review: %s
                Game: %s
                Features: %s
                """.formatted(
                        dto.ratings(),
                        truncate(dto.reviewText(), 1500),
                        dto.title(),
                        dto.features()

                ))
                .collect(Collectors.joining("\n---\n"));
        return input;
    }

    public String RetrievallmjudgeInstructions(){
        return """
                You are evaluating retrieval quality for a game recommendation system.
                
                Given:
                - User preference query: {query}
                - Top 3 retrieved games: {games}
                
                Evaluate each game independently based on how relevant it is to the user's overall preferences.
                
                Count a game as "good" if it is a relevant recommendation and meaningfully matches the user's preferences.
                Count it as "bad" if it has little or no meaningful relevance to the user's preferences.
                
                Do not judge based only on rating or vector distance.
                
                Return ONLY valid JSON:
                {
                  "goodRetrieval": <number>,
                  "badRetrieval": <number>
                }
                
                The two numbers must add up to 3.
                """;
    }


}
