package com.onion.backend.exception;

import org.springframework.http.HttpStatus;

public class ServerException extends BaseException{

    public ServerException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
