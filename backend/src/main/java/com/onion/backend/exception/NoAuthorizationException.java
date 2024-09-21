package com.onion.backend.exception;

import org.springframework.http.HttpStatus;

public class NoAuthorizationException extends BaseException {

    public NoAuthorizationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
