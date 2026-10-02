package com.siddu.gamesense.repository;

import com.siddu.gamesense.Entities.UserGame;
import com.siddu.gamesense.dto.ReviewsResponse;
import com.siddu.gamesense.dto.UserGameReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {
    @Query("SELECT u.sourceKey FROM UserGame u")
    List<String> findAllSourceKeys();

    @Query("""
            SELECT new com.siddu.gamesense.dto.UserGameReviewDTO(
            ug.rating,
            ug.reviewText,
            g.parentAsin,
            gm.title,
            gm.features
        )
        FROM UserGame ug
        JOIN ug.game g
        JOIN g.metadata gm
        WHERE ug.user.userId = :userId
        ORDER BY ug.timestamp DESC
        """)
    List<UserGameReviewDTO> findRecentUserReviews(
            @Param("userId") String userId,
            Pageable pageable
    );

    @Query("""
        SELECT new com.siddu.gamesense.dto.UserGameReviewDTO(
            ug.rating,
            ug.reviewText,
            g.parentAsin,
            gm.title,
            gm.features
        )
        FROM UserGame ug
        JOIN ug.game g
        JOIN g.metadata gm
        WHERE ug.user.userId = :userId
        ORDER BY ug.rating DESC, ug.timestamp DESC
        """)
    List<UserGameReviewDTO> findTopRatedUserReviews(
            @Param("userId") String userId,
            Pageable pageable
    );

    @Query("""
    SELECT new com.siddu.gamesense.dto.ReviewsResponse(
        g.parentAsin,
        m.title,
        m.thumbnail,
        ug.rating,
        ug.reviewText
    )
    FROM UserGame ug
    JOIN ug.game g
    JOIN g.metadata m
    WHERE ug.user.userId = :userId
    ORDER BY ug.timestamp DESC
""")
    Page<ReviewsResponse> findUserReviews(
            @Param("userId") String userId,
            Pageable pageable
    );




}
