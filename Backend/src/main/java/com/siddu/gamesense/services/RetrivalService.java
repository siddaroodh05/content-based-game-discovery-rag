package com.siddu.gamesense.services;


import com.siddu.gamesense.dto.RecommendedGameDTO;
import com.siddu.gamesense.repository.GameRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrivalService {
    private final GameRepository gameRepository;
    private final embeddingService embeddingService;
    @Value("${retrieval.embedding.tok-k}")
    private  Integer topK;
     public RetrivalService(GameRepository gameRepository,
                            embeddingService embeddingService) {
        this.gameRepository = gameRepository;
        this.embeddingService = embeddingService;

    }

    public List<RecommendedGameDTO>  GetRecommendedGames( String query){



        return gameRepository.findSimilarGames(embeddingService.queryembed(query),topK);



    }
}
