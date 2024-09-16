package com.onion.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(BaseException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
