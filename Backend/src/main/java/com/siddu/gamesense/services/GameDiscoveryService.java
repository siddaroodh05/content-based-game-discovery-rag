package com.siddu.gamesense.services;

import com.siddu.gamesense.Entities.Game;
import com.siddu.gamesense.dto.GameCardDTO;
import com.siddu.gamesense.dto.GameMetadata;
import com.siddu.gamesense.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GameDiscoveryService {

    private final GameRepository gameRepository;

    @Autowired
    GameDiscoveryService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Page<GameCardDTO> getGameCardData(int page){
        Pageable pageable = PageRequest.of(
                page,
                10
        );

        return gameRepository.findGameCards(pageable);

    }

    public GameMetadata  getGameMetadata(String ParentAasin){

        Game game=gameRepository.findByParentAsinWithMetadata(ParentAasin).orElseThrow(()-> new RuntimeException("Game not found"));

        return new GameMetadata(game.getMetadata().getTitle(),
                game.getMetadata().getThumbnail(),
                game.getMetadata().getFeatures(),
                game.getMetadata().getAverageRating(),
                game.getMetadata().getCategories());

    }
}
