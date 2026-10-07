package com.siddu.gamesense.controller;

import com.siddu.gamesense.dto.*;
import com.siddu.gamesense.dto.Request.CustomQueryRequest;
import com.siddu.gamesense.dto.Request.GameMetadata;
import com.siddu.gamesense.dto.Request.LoginRequest;
import com.siddu.gamesense.dto.Request.UserreviewsRequest;
import com.siddu.gamesense.services.*;
import com.siddu.gamesense.utils.EvaluationUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
public class GamesenseController {

    private final DataIngestionService dataIngestionService;
    private final UserReviewService userReviewService;
    private final EvalutionService evaluationService;
    private final EvaluationUsers evaluationUsers;
    private final GameDiscoveryService gameDiscoveryService;
    private final UserService userService;
    private final RetrivalService retrivalService;


    @Autowired
    public GamesenseController(DataIngestionService dataIngestionService,
                               UserReviewService userReviewService,
                              EvalutionService evaluationService,
                               EvaluationUsers evaluationUsers,
                               GameDiscoveryService gameDiscoveryService,
                               UserService userService,
                               RetrivalService retrivalService

    ) {
        this.dataIngestionService = dataIngestionService;
        this.userReviewService = userReviewService;
        this.evaluationService = evaluationService;
        this.evaluationUsers = evaluationUsers;
        this.gameDiscoveryService = gameDiscoveryService;
        this.userService = userService;
        this.retrivalService = retrivalService;
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
        evaluationUsers.createEvaluation();
        return ResponseEntity.accepted().body("started");
    }

    @PostMapping("/gamesense/games/evaluate_precision")
    public ResponseEntity<RetrievalPrecision> UserReviewsGamesEvaluationPrecision () {

        return ResponseEntity.ok(evaluationService.calculateRetrievalPrecision());

    }

    @GetMapping("/games/cards")
    public ResponseEntity<Page<GameCardDTO>> getGameCards(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(gameDiscoveryService.getGameCardData(page));
    }

    @PostMapping("/games/metadata")
    public ResponseEntity<com.siddu.gamesense.dto.GameMetadata> getGameMetadata(@RequestBody()GameMetadata request) {
        return ResponseEntity.ok(gameDiscoveryService.getGameMetadata(request.ParentAsin()));

    }

    @PostMapping("/games/user/reviews")
    public ResponseEntity<Page<ReviewsResponse>> UserReviews(@RequestBody() UserreviewsRequest request,
                                                             @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.GetUserReviews(request,page));
    }
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody() LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/games/query")
    public ResponseEntity<List<GameCardDTO>> getGamesOnCustomQuery(@RequestBody() CustomQueryRequest request) {
        return ResponseEntity.ok(retrivalService.getRecommendedGamesOnUserQuery(request.query()));


    }
}
