package com.onion.backend.dto.response;

import com.onion.backend.entity.Comment;

public record CommentResponse(
        Long commentId,
        String contents,
        Long authorId,
        String authorName
) {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername()
        );
    }
}
