package com.siddu.gamesense.repository;
import com.siddu.gamesense.Entities.Game;
import com.siddu.gamesense.dto.RecommendedGameDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByparentAsin(String parentAsin);

    @Query("SELECT g.parentAsin FROM Game g")
    List<String> findAllParentAsins();

    @Query(value = """
            SELECT
            g.parent_asin AS parentAsin,
            gm.title AS title,
            gm.categories AS categories,
            gm.features AS features,
            gm.average_rating AS averageRating,
            gm.rating_number AS ratingNumber,
            gm.embedding <=> CAST(:embedding AS vector) AS distance
        FROM games g
        JOIN game_metadata gm
            ON gm.game_id = g.id
        ORDER BY gm.embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<RecommendedGameDTO> findSimilarGames(
            @Param("embedding") float[] embedding,
            @Param("limit") int limit
    );
}
