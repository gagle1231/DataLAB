package com.onion.backend.dto.response;

import com.onion.backend.entity.Article;

public record ArticleResponse(
        Long id,
        String title,
        String authorUsername,
        String authorEmail,
        String content
) {

    public ArticleResponse(Article article) {
        this(
                article.getId(),
                article.getTitle(),
                article.getAuthor().getUsername(),
                article.getAuthor().getEmail(),
                article.getContent()
        );
    }
}
