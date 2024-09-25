package com.onion.backend.dto.response;

import com.onion.backend.dto.response.CommentResponse;
import com.onion.backend.entity.Article;

import java.util.List;

public record ArticleResponse(
        Long id,
        String title,
        String authorUsername,
        String authorEmail,
        String content,
        List<CommentResponse> commentList
) {
    public static ArticleResponse of(Article article, List<CommentResponse> commentList) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getAuthor().getUsername(),
                article.getAuthor().getEmail(),
                article.getContent(),
                commentList
        );
    }

    public static ArticleResponse of(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getAuthor().getUsername(),
                article.getAuthor().getEmail(),
                article.getContent(),
                null
        );
    }
}
