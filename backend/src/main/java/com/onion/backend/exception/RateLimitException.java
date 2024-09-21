package com.onion.backend.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends BaseException{

    public RateLimitException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
