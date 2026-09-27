package com.siddu.gamesense.services;


import com.siddu.gamesense.dto.RetrievedResult;
import com.siddu.gamesense.dto.UserGameReviewDTO;
import com.siddu.gamesense.repository.UserGameRepository;
import com.siddu.gamesense.utils.InstructionPrompts;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserReviewService {
    private final UserGameRepository userGameRepository;
    private final LlmService llmService;
    private final InstructionPrompts instructionPrompts;
    private final RetrivalService retrivalService;
    public UserReviewService(UserGameRepository userGameRepository,
                             LlmService llmService,
                             InstructionPrompts instructionPrompts,
                             RetrivalService retrivalService) {
        this.userGameRepository = userGameRepository;
        this.llmService = llmService;
        this.instructionPrompts = instructionPrompts;
        this.retrivalService = retrivalService;
    }

    public RetrievedResult getUserRecentGameReviews(String userId) {


        Pageable pageable = PageRequest.of(0, 3);
        List<UserGameReviewDTO> response= userGameRepository.findRecentUserReviews(
                        userId,
                        pageable
                );

        if(response.isEmpty()){
            return  null;
        }
        String query=llmService.PraseQuery(instructionPrompts.prasegamereviewdto(response),
                instructionPrompts.queryinstructions());

        return  new RetrievedResult(query,retrivalService.GetRecommendedGames(query));


    }

    public RetrievedResult  getUserTopRatingGameReviews(String userId) {
        Pageable pageable = PageRequest.of(0, 3);

        List<UserGameReviewDTO> reviews=userGameRepository.findTopRatedUserReviews(userId, pageable);
        if(reviews.isEmpty()){
            return null;
        }
        String query=llmService.PraseQuery(instructionPrompts.prasegamereviewdto(reviews),
                instructionPrompts.queryinstructions());


        return   new RetrievedResult(query,retrivalService.GetRecommendedGames(query));
    }


}
