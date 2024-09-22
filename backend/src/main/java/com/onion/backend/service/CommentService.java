package com.onion.backend.service;

import com.onion.backend.dto.request.CommentRequest;
import com.onion.backend.entity.Article;
import com.onion.backend.entity.Board;
import com.onion.backend.entity.Comment;
import com.onion.backend.entity.User;
import com.onion.backend.exception.RateLimitException;
import com.onion.backend.exception.ResourceNotFoundException;
import com.onion.backend.repository.ArticleRepository;
import com.onion.backend.repository.BoardRepository;
import com.onion.backend.repository.CommentRepository;
import com.onion.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Comment writeComment(Long boardId, Long articleId, CommentRequest request, UserDetails userDetails) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found or has been deleted"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if(article.isDeleted()){
            throw new ResourceNotFoundException("Article has been deleted");
        }

        User author = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!validateCommentCreation(author.getEmail())) {
            throw new RateLimitException("You can only post comments once every minute.");
        }

        Comment comment = Comment.builder()
                .content(request.content())
                .author(author)
                .article(article)
                .build();

        return commentRepository.save(comment);
    }

    private boolean validateCommentCreation(String email) {
        Optional<LocalDateTime> latestCommentDate = commentRepository.findLatestCommentByAuthor(email);
        return latestCommentDate.map(this::isCreatable).orElse(true);
    }

    private boolean isCreatable(LocalDateTime lastCommentDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastCommentDate, now);
        long minutesSinceLastComment = duration.toMinutes();
        return minutesSinceLastComment >= 1;
    }

}
