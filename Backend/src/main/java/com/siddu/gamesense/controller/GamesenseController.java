package com.siddu.gamesense.controller;

import com.siddu.gamesense.dto.RetrievedResult;
import com.siddu.gamesense.dto.UserReviewRequest;
import com.siddu.gamesense.services.DataIngestionService;
import com.siddu.gamesense.services.EvalutionService;
import com.siddu.gamesense.services.UserReviewService;
import com.siddu.gamesense.utils.EvaluationUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
public class GamesenseController {

    private final DataIngestionService dataIngestionService;
    private final UserReviewService userReviewService;
    private final EvalutionService evaluationService;
    private final EvaluationUsers evaluationUsers;

    @Autowired
    public GamesenseController(DataIngestionService dataIngestionService,
                               UserReviewService userReviewService,
                              EvalutionService evaluationService,
                               EvaluationUsers evaluationUsers
    ) {
        this.dataIngestionService = dataIngestionService;
        this.userReviewService = userReviewService;
        this.evaluationService = evaluationService;
        this.evaluationUsers = evaluationUsers;
    }

    @PostMapping("/gamesense/metadata-ingest")
    public ResponseEntity<String> GameMetadataIngest() throws IOException {
        dataIngestionService.ingestGames();
        return ResponseEntity.accepted().body("gamesense ingested started");
    }

    @PostMapping("/gamesense/users-ingest")
    public ResponseEntity<String> UserMetadataIngest() throws IOException {
        dataIngestionService.ingestUserHistory();
        return ResponseEntity.accepted().body("gamesense ingested started");
    }

    @PostMapping("/gamesense/games/recent_recommendation")
    public ResponseEntity<RetrievedResult> UserReviewsGamesRecentRecommendations(
            @RequestBody()UserReviewRequest request)  {
        return ResponseEntity.ok(userReviewService.getUserRecentGameReviews(request.userId()));
    }

    @PostMapping("/gamesense/games/top_rated_recommendation")
    public ResponseEntity<RetrievedResult> UserTopRatedReviewsGamesRecommendations (
            @RequestBody()UserReviewRequest request)  {
        return ResponseEntity.ok(userReviewService.getUserTopRatingGameReviews(request.userId()));

    }

    @PostMapping("/gamesense/games/evaluation")
    public ResponseEntity<String> UserReviewsGamesEvaluation () throws IOException {
        evaluationService.evaluation();
        return ResponseEntity.accepted().body("started");
    }

    @PostMapping("/gamesense/games/evalution_users")
    public ResponseEntity<String> UserReviewsGamesEvaluationUsers () throws IOException {
        evaluationUsers.createEvaluationfile();
        return ResponseEntity.accepted().body("started");
    }

}
