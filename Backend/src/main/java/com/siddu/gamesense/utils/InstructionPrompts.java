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
                Generate exactly ONE search query representing 2–3 distinct important
                preferences found in the provided games and reviews.
                
                Each preference should be a compact phrase containing the main semantic
                keywords/features, for example:
                - sci-fi multiplayer combat
                - horror thriller with adaptive mechanics
                - anime-style story-driven RPG
                
                Do not combine unrelated preferences into one sentence.
                Do not focus on only one game or one feature.
                Prefer concrete gameplay mechanics, genres, themes, visual/style characteristics,
                story characteristics, or multiplayer characteristics that appear meaningful
                in the user's provided games/reviews.
                Separate each preference with a comma.
                
                The query should be concise, under 30 tokens.
                Return ONLY the query as plain text.
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
                
                The query contains separate preference themes. Count a game as "good"
                if it meaningfully matches ANY ONE of those themes. It does not need
                to match the other themes.
                
                Count as "bad" only if the game has little or no meaningful relevance
                to any theme.
                
                Judge based on gameplay, genre, mechanics, progression, themes, story,
                multiplayer style, and other meaningful characteristics.
                
                Do not judge based only on word overlap, title similarity, rating,
                popularity, or vector distance.
                
                Return ONLY valid JSON:
                {
                  "goodRetrieval": <number>,
                  "badRetrieval": <number>
                }
                
                The two numbers must add up to 3.
                """;
    }

    public String qualitycheckinstructions() {
        return """
        Evaluate the retrieved games against the user's query.

        For each game, compare its title and features with the user's query.
        Return the parentAsin of every game that meaningfully matches the query.

        Consider the requested gameplay, genre, mechanics, setting, player mode,
        or other important requirements.

        Ignore superficial keyword matches, shared franchises, platforms,
        ratings, and popularity.

        IMPORTANT:
        - Do not explain your reasoning.
        - Return ONLY the matching parentAsin values.
        - Separate multiple parentAsin values with commas.
        - Do not add spaces, brackets, quotes, labels, or other text.
        - If no games match, return exactly: null

        Example with multiple matches:
        B00005MKYU,B004D1Z3UO

        Example with one match:
        B00005MKYU

        Example with no matches:
        null
        """;
    }

}
