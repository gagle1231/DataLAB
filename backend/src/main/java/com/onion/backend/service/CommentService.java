package com.onion.backend.service;

import com.onion.backend.dto.request.CommentRequest;
import com.onion.backend.entity.Article;
import com.onion.backend.entity.Board;
import com.onion.backend.entity.Comment;
import com.onion.backend.entity.User;
import com.onion.backend.exception.ForbiddenException;
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

    /**
     * 댓글 작성 메소드.
     *
     * @param boardId    보드 ID
     * @param articleId  아티클 ID
     * @param request    댓글 요청 정보
     * @param userDetails 사용자 정보
     * @return 작성된 댓글
     */
    @Transactional
    public Comment writeComment(Long boardId,
                                Long articleId,
                                CommentRequest request,
                                UserDetails userDetails) {
        Board board = findBoardById(boardId);
        Article article = findArticleById(articleId);
        User author = findUserByEmail(userDetails.getUsername());

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

    /**
     * 댓글 수정 메소드.
     *
     * @param boardId    보드 ID
     * @param articleId  아티클 ID
     * @param commentId  댓글 ID
     * @param commentRequest 수정할 댓글 요청 정보
     * @param userDetails 사용자 정보
     */
    @Transactional
    public void editComment(Long boardId,
                            Long articleId,
                            Long commentId,
                            CommentRequest commentRequest,
                            UserDetails userDetails) {
        validateBoardAndArticleExistence(boardId, articleId);
        Comment comment = findCommentById(articleId, commentId);

        User author = findUserByEmail(userDetails.getUsername());
        validateCommentAuthor(comment, author);
        if (!validateCommentCreation(author.getEmail())) {
            throw new RateLimitException("You can only edit comments once every minute.");
        }
        comment.sectContent(commentRequest.content());
        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제 메소드.
     *
     * @param boardId    보드 ID
     * @param articleId  아티클 ID
     * @param commentId  댓글 ID
     * @param userDetails 사용자 정보
     */
    @Transactional
    public void deleteComment(Long boardId,
                              Long articleId,
                              Long commentId,
                              UserDetails userDetails) {
        validateBoardAndArticleExistence(boardId, articleId);
        Comment comment = findCommentById(articleId, commentId);
        User author = findUserByEmail(userDetails.getUsername());
        validateCommentAuthor(comment, author);
        comment.softDelete();
        commentRepository.save(comment);

    }

    private void validateBoardAndArticleExistence(Long boardId, Long articleId) {
        findBoardById(boardId);
        findArticleById(articleId);
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

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found or has been deleted"));
    }

    private Article findArticleById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (article.isDeleted()) {
            throw new ResourceNotFoundException("Article has been deleted");
        }
        return article;
    }

    private Comment findCommentById(Long articleId, Long commentId) {
        Comment comment = commentRepository.findByArticleIdAndId(articleId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (comment.isDeleted()) {
            throw new ResourceNotFoundException("Comment has been deleted");
        }
        return comment;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateCommentAuthor(Comment comment, User author) {
        if (!comment.getAuthor().getEmail().equals(author.getEmail())) {
            throw new ForbiddenException("You are not authorized to edit this comment");
        }
    }

}
