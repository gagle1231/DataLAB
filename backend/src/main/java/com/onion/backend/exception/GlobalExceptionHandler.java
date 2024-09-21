package com.onion.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(BaseException ex) {
        ErrorResponse errorResponse = ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
        log.error(ex.getStatus()+":: "+ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
