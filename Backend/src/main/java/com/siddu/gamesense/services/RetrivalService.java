package com.siddu.gamesense.services;


import com.siddu.gamesense.dto.ChunkDebugDTO;
import com.siddu.gamesense.dto.GameCardDTO;
import com.siddu.gamesense.dto.RecommendedGameDTO;
import com.siddu.gamesense.repository.GameRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RetrivalService {
    private final GameRepository gameRepository;
    private final embeddingService embeddingService;
    private final LlmService llmService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${retrieval.embedding.tok-k}")
    private Integer topK;

    public RetrivalService(GameRepository gameRepository,
                           embeddingService embeddingService,
                           LlmService llmService) {
        this.gameRepository = gameRepository;
        this.embeddingService = embeddingService;
        this.llmService = llmService;
    }

    @Transactional
    public List<RecommendedGameDTO> GetRecommendedGames(String query) {

        entityManager.createNativeQuery("SET LOCAL hnsw.ef_search = 100").executeUpdate();

        return gameRepository.findSimilarGames(embeddingService.embed(query), topK, 100);

    }
    @Transactional
    public List<GameCardDTO> GetRecommendedGamesonuserQuery(String query) {

        entityManager.createNativeQuery("SET LOCAL hnsw.ef_search = 100").executeUpdate();
        List<RecommendedGameDTO> recommendedGames =gameRepository.findSimilarGames(embeddingService.embed(query), topK, 100);
        List<String> games=llmService.filteroutRecommendedGames(recommendedGames,query);
        if (games.isEmpty()) {
            return List.of();
        }
        return gameRepository.findGameCardsByParentAsins(games);
    }
}