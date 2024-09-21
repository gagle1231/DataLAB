package com.onion.backend.dto.request;

public record WriteArticleRequest (
        String title,
        String content
){
}
