package com.siddu.gamesense.Exceptions;

import com.siddu.gamesense.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GamenotfoundException.class)
    public ResponseEntity<ApiErrorResponse> handleGameNotFoundException(GamenotfoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse(HttpStatus.NOT_FOUND.name(), e.getMessage()));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse(HttpStatus.NOT_FOUND.name(), e.getMessage()));
    }
    @ExceptionHandler(ReviewsNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleReviewsNotFoundException(ReviewsNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse(HttpStatus.NOT_FOUND.name(), e.getMessage()));
    }
}
