package com.siddu.gamesense.services;

import com.siddu.gamesense.Entities.Game;
import com.siddu.gamesense.Entities.GameChunk;
import com.siddu.gamesense.dto.GameMetadataCsvRow;
import com.siddu.gamesense.enums.ChunkType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChunkingService {

    private static final Set<String> BOILERPLATE_PATTERNS = Set.of(
            "works great", "no problems", "comes with the case",
            "comes with the manual", "great condition", "as described",
            "fast shipping", "works perfectly", "no issues",
            "brand new", "like new", "good condition"
    );

    public List<GameChunk> buildChunks(Game game, GameMetadataCsvRow row) {
        List<GameChunk> chunks = new ArrayList<>();

        chunks.add(buildIdentityChunk(game, row));


        chunks.addAll(buildFeatureChunks(game, row));

        return chunks;
    }

    private GameChunk buildIdentityChunk(Game game, GameMetadataCsvRow row) {
        String categories = row.categories() != null ? row.categories() : "";
        String identityText = row.title() + " | " + categories;

        return GameChunk.builder()
                .id(game.getParentAsin() + "_identity")
                .game(game)
                .chunkType(ChunkType.IDENTITY)
                .text(identityText)
                .build();
    }

    private List<GameChunk> buildFeatureChunks(Game game, GameMetadataCsvRow row) {
        List<String> features = splitFeatures(row.title(), row.features());
        List<GameChunk> chunks = new ArrayList<>();

        for (int i = 0; i < features.size(); i++) {
            GameChunk chunk = GameChunk.builder()
                    .id(game.getParentAsin() + "_f" + (i + 1))
                    .game(game)
                    .chunkType(ChunkType.FEATURE)
                    .text(features.get(i))
                    .build();
            chunks.add(chunk);
        }

        return chunks;
    }

    private List<String> splitFeatures(String title, String rawFeatures) {
        if (rawFeatures == null || rawFeatures.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rawFeatures.split("\\|"))
                .map(String::trim)
                .filter(f -> !f.isBlank())
                .filter(f -> f.length() > 3)
                .filter(f -> !isBoilerplate(f))
                .map(f -> title + ": " + f)
                .collect(Collectors.toList());
    }

    private boolean isBoilerplate(String feature) {
        String normalized = feature.toLowerCase().trim();
        return BOILERPLATE_PATTERNS.stream().anyMatch(normalized::contains);
    }
}