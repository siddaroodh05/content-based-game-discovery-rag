package com.siddu.gamesense.services;

import com.siddu.gamesense.Entities.User;
import com.siddu.gamesense.Exceptions.UserNotFoundException;
import com.siddu.gamesense.dto.LoginResponse;
import com.siddu.gamesense.dto.Request.LoginRequest;
import com.siddu.gamesense.dto.Request.UserreviewsRequest;
import com.siddu.gamesense.dto.ReviewsResponse;
import com.siddu.gamesense.repository.UserGameRepository;
import com.siddu.gamesense.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserGameRepository userGameRepository;

    public UserService(UserRepository userRepository,
                       UserGameRepository userGameRepository) {
        this.userRepository = userRepository;
        this.userGameRepository = userGameRepository;
    }

    public Page<ReviewsResponse> GetUserReviews( UserreviewsRequest request,int page) {
        Pageable pageable= PageRequest.of(page,10);

        return userGameRepository.findUserReviews(request.userId(),pageable);

    }

    public LoginResponse login (LoginRequest loginRequest) {

        User user=userRepository.findByUserId(loginRequest.userId()).orElseThrow(
                ()->new UserNotFoundException("User not found!")
        );
        return  new LoginResponse(user.getUserId());
    }
}
