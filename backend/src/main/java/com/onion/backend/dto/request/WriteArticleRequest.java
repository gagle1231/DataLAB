package com.onion.backend.dto.request;

import lombok.Getter;

public record WriteArticleRequest (
        Long boardId,
        String title,
        String content
){
}
