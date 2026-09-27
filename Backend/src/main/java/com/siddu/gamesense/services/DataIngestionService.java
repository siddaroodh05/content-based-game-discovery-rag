package com.siddu.gamesense.services;

import com.siddu.gamesense.Entities.Game;
import com.siddu.gamesense.Entities.GameMetadata;
import com.siddu.gamesense.Entities.User;
import com.siddu.gamesense.Entities.UserGame;
import com.siddu.gamesense.dto.GameMetadataCsvRow;
import com.siddu.gamesense.dto.UserGameCsvRow;
import com.siddu.gamesense.repository.GameRepository;
import com.siddu.gamesense.repository.UserGameRepository;
import com.siddu.gamesense.repository.UserRepository;
import com.siddu.gamesense.utils.CsvReader;
import com.siddu.gamesense.utils.AutoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataIngestionService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserGameRepository userGameRepository;
    private final embeddingService embeddingService;
    private final CsvReader csvReader;
    private final AutoMapper csvdtomapper;

    @Autowired
    public DataIngestionService(CsvReader csvReader, GameRepository gameRepository,
                                UserRepository userRepository,
                                UserGameRepository userGameRepository, embeddingService embeddingService,
                                AutoMapper csvdtomapper) {
        this.csvReader = csvReader;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userGameRepository = userGameRepository;
        this.embeddingService = embeddingService;
        this.csvdtomapper = csvdtomapper;
    }

    @Async
    public void ingestGames() throws IOException {

        Set<String> existingParentAsins =
                new HashSet<>(gameRepository.findAllParentAsins());
        AtomicReference<Long> processed= new AtomicReference<>((long) 0);

        csvReader.read("games_metadata.csv", record -> {

            String parentAsin = record.get("parent_asin");

            if (existingParentAsins.contains(parentAsin)) {
               processed.updateAndGet(v -> v + 1);
                log.info("already processed games: {}", processed.get());
                return;

            }

            GameMetadataCsvRow row = csvdtomapper.mapper(record);

            Game game = Game.builder()
                    .parentAsin(row.parentAsin())
                    .build();


            float[] embedding = embeddingService.embed(row);

            GameMetadata metadata = GameMetadata.builder()
                    .game(game)
                    .title(row.title())
                    .categories(row.categories())
                    .description(row.description())
                    .features(row.features())
                    .averageRating(row.averageRating())
                    .ratingNumber(row.ratingNumber())
                    .embedding(embedding)
                    .build();

            game.setMetadata(metadata);
            gameRepository.save(game);
            existingParentAsins.add(game.getParentAsin());
            log.info("Saved game: {}", parentAsin);
        });

        log.info("completed ingesting games");

    }

    @Async
    public void ingestUserHistory() throws IOException {

        Set<String> existingSourceKeys =
                new HashSet<>(userGameRepository.findAllSourceKeys());

        Map<String, User> existingUsers =
                userRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                User::getUserId,
                                user -> user
                        ));

        csvReader.read("history.csv", record -> {

            UserGameCsvRow row =
                    csvdtomapper.historymapper(record);

            String userId = row.userId();

            User user = existingUsers.get(userId);

            if (user == null) {

                user = User.builder()
                        .userId(userId)
                        .build();

                userRepository.save(user);

                existingUsers.put(userId, user);

                log.info("Created user: {}", userId);
            }

            Game game = gameRepository
                    .findByparentAsin(row.parentAsin())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Game not found: " + row.parentAsin()
                            ));
            String sourceKey =
                    row.userId() + "_" +
                            row.parentAsin() + "_" +
                            row.timestamp();

            if (existingSourceKeys.contains(sourceKey)) {
                log.info("Skipping existing source: {}", sourceKey);
                return;
            }

            UserGame userGame = UserGame.builder()
                    .user(user)
                    .game(game)
                    .rating(row.rating())
                    .reviewText(row.reviewText())
                    .sourceKey(sourceKey)
                    .timestamp(Instant.ofEpochMilli(row.timestamp()))
                    .build();

            userGameRepository.save(userGame);
        });
    }

}