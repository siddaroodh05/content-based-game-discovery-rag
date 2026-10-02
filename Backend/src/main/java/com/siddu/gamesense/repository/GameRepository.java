package com.siddu.gamesense.repository;
import com.siddu.gamesense.Entities.Game;
import com.siddu.gamesense.dto.ChunkDebugDTO;
import com.siddu.gamesense.dto.GameCardDTO;
import com.siddu.gamesense.dto.RecommendedGameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        MIN(c.distance) AS distance
    FROM (
        SELECT gc.game_id,
               gc.embedding <=> CAST(:embedding AS vector) AS distance
        FROM game_chunks gc
        WHERE gc.embedding IS NOT NULL
        ORDER BY gc.embedding <=> CAST(:embedding AS vector)
        LIMIT :chunkLimit
    ) c
    JOIN games g ON g.id = c.game_id
    JOIN game_metadata gm ON gm.game_id = g.id
    GROUP BY g.id, g.parent_asin, gm.title, gm.categories,
             gm.features, gm.average_rating, gm.rating_number
    ORDER BY MIN(c.distance)
    LIMIT :limit
    """, nativeQuery = true)
    List<RecommendedGameDTO> findSimilarGames(
            @Param("embedding") float[] embedding,
            @Param("limit") int limit,
            @Param("chunkLimit") int chunkLimit);


    @Query(value = """
    SELECT
        g.parent_asin AS parentAsin,
        gm.title AS title,
        gc.chunk_type AS chunkType,
        gc.text AS text,
        gc.embedding <=> CAST(:embedding AS vector) AS distance
    FROM game_chunks gc
    JOIN games g
        ON g.id = gc.game_id
    JOIN game_metadata gm
        ON gm.game_id = g.id
    WHERE gc.embedding IS NOT NULL
    ORDER BY gc.embedding <=> CAST(:embedding AS vector)
    LIMIT :limit
    """, nativeQuery = true)
    List<ChunkDebugDTO> findSimilarChunks(
            @Param("embedding") float[] embedding,
            @Param("limit") int limit
    );

    @Query("""
        SELECT new com.siddu.gamesense.dto.GameCardDTO(
            g.parentAsin,
            m.title,
            m.thumbnail,
            m.averageRating
        )
        FROM Game g
        JOIN g.metadata m
        ORDER BY g.id
    """)
    Page<GameCardDTO> findGameCards(Pageable pageable);

    @Query("""
    SELECT g
    FROM Game g
    JOIN FETCH g.metadata
    WHERE g.parentAsin = :parentAsin
""")
    Optional<Game> findByParentAsinWithMetadata(
            @Param("parentAsin") String parentAsin
    );

    @Query("""
    SELECT new com.siddu.gamesense.dto.GameCardDTO(
        g.parentAsin,
        m.title,
        m.thumbnail,
        m.averageRating
    )
    FROM Game g
    JOIN g.metadata m
    WHERE g.parentAsin IN :parentAsins
    """)
    List<GameCardDTO> findGameCardsByParentAsins(
            @Param("parentAsins") List<String> parentAsins
    );
}
