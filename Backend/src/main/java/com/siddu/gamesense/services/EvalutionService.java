package com.siddu.gamesense.services;

import com.siddu.gamesense.dto.*;
import com.siddu.gamesense.utils.CsvReader;
import com.siddu.gamesense.utils.AutoMapper;
import com.siddu.gamesense.utils.EvalResultReadWriteService;
import com.siddu.gamesense.utils.InstructionPrompts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class EvalutionService {

    private final UserReviewService userReviewService;
    private final CsvReader csvReader;
    private final AutoMapper csvdtomapper;
    private final EvalResultReadWriteService evalResultReadWriteService;
    private  final LlmService llmService;
    private final InstructionPrompts  instructionPromptsService;


    EvalutionService(
            UserReviewService userReviewService,
            CsvReader csvReader,
            AutoMapper csvdtomapper,
            EvalResultReadWriteService evalResultReadWriteService,
            LlmService llmService,
            InstructionPrompts  instructionPromptsService) {

        this.userReviewService = userReviewService;
        this.csvReader = csvReader;
        this.csvdtomapper = csvdtomapper;
        this.evalResultReadWriteService = evalResultReadWriteService;
        this.llmService = llmService;
        this.instructionPromptsService = instructionPromptsService;

    }

    @Async
    public void evaluation() throws IOException {

        Set<String> userIds=evalResultReadWriteService.loadExistingEvaIds();

        csvReader.read("evaluation_users.csv", record -> {

            String userId =
                    csvdtomapper.EvaluationMapper(record);

            if (userIds.contains(userId)) {
                return;
            }

            long startTime = System.currentTimeMillis();

            RetrievedResult recentRecommendations =
                    userReviewService.getUserRecentGameReviews(userId);

            long recentLatency =
                    System.currentTimeMillis() - startTime;


            startTime = System.currentTimeMillis();

            RetrievedResult topRatedRecommendations =
                    userReviewService.getUserTopRatingGameReviews(userId);

            long topRatedLatency =
                    System.currentTimeMillis() - startTime;



            RetrievalQuality RecentRatedBasedQuality=llmService.RetrievalQualityJudge
                    (instructionPromptsService.RetrievallmjudgeInstructions(),recentRecommendations);


            RetrievalQuality TopRatedBasedQuality=llmService.RetrievalQualityJudge(instructionPromptsService.RetrievallmjudgeInstructions(),topRatedRecommendations);

            ChannelResult recentChannel =
                    new ChannelResult(
                         getparentasins(recentRecommendations.games()),
                            RecentRatedBasedQuality.goodRetrieval(),
                            RecentRatedBasedQuality.badRetrieval(),
                            recentLatency
                    );

            ChannelResult topRatedChannel =
                    new ChannelResult(
                            getparentasins(topRatedRecommendations.games()),
                            TopRatedBasedQuality.goodRetrieval(),
                            TopRatedBasedQuality.badRetrieval(),
                            topRatedLatency
                    );

            EvalCaseResult result =
                    new EvalCaseResult(
                            userId,
                            recentChannel,
                            topRatedChannel,
                            topRatedChannel.goodRetrieval()+recentChannel.goodRetrieval(),
                            topRatedChannel.badRetrieval()+recentChannel.badRetrieval()
                    );
            evalResultReadWriteService.appendResult(result);
            userIds.add(userId);

            try {

                log.info("waiting 50 seconds for next request ,evaluated : {}", userId);
                Thread.sleep(50_000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Evaluation interrupted", e);
            }


        });

    }


    public  List<String> getparentasins(List<RecommendedGameDTO> games) {
        List<String> parentasins = new ArrayList<>();
        for (RecommendedGameDTO game : games) {
            parentasins.add(game.parentAsin());
        }
        return parentasins;
    }

    public RetrievalPrecision calculateRetrievalPrecision() {

       List<EvaluatedResult> evaluatedResults =
                evalResultReadWriteService.loadEvaluatedResults();

        int topRatedGood = 0;
        int topRatedTotal = 0;

        int recentBasedGood = 0;
        int recentBasedTotal = 0;

        for (EvaluatedResult result : evaluatedResults) {

            RetrievalQuality topRated =
                    result.TopRatedRetrieval();

            RetrievalQuality recentBased =
                    result.recentBasedRetrieval();

            topRatedGood += topRated.goodRetrieval();
            topRatedTotal +=
                    topRated.goodRetrieval()
                            + topRated.badRetrieval();

            recentBasedGood += recentBased.goodRetrieval();
            recentBasedTotal +=
                    recentBased.goodRetrieval()
                            + recentBased.badRetrieval();
        }

        double topRatedPrecision =
                topRatedTotal == 0
                        ? 0.0
                        : (double) topRatedGood / topRatedTotal;

        double recentBasedPrecision =
                recentBasedTotal == 0
                        ? 0.0
                        : (double) recentBasedGood / recentBasedTotal;

        int overallGood =
                topRatedGood + recentBasedGood;

        int overallTotal =
                topRatedTotal + recentBasedTotal;

        double overallPrecision =
                overallTotal == 0
                        ? 0.0
                        : (double) overallGood / overallTotal;

        return new RetrievalPrecision(
                topRatedPrecision,
                recentBasedPrecision,
                overallPrecision
        );

    }




}