package com.onion.backend.service;

import com.onion.backend.dto.request.UpdateArticleRequest;
import com.onion.backend.dto.request.WriteArticleRequest;
import com.onion.backend.dto.response.ArticleResponse;
import com.onion.backend.dto.response.CommentResponse;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ArticleResponse writeArticle(Long boardId, WriteArticleRequest request, UserDetails userDetails) {
        Board board = findBoardById(boardId);
        User author = findUserByEmail(userDetails.getUsername());

        validateCreateRateLimit(author.getEmail());

        Article article = Article.builder()
                .title(request.title())
                .author(author)
                .board(board)
                .content(request.content())
                .build();

        return ArticleResponse.of(articleRepository.save(article));
    }

    public List<ArticleResponse> getTopArticles(Long boardId, Long firstId, Long lastId) {
        List<Article> articles;
        if (firstId != null) {
            articles = articleRepository.findNextArticles(boardId, firstId);
        } else if (lastId != null) {
            articles = articleRepository.findPreviousArticles(boardId, lastId);
        } else {
            articles = articleRepository.findTop10ByBoardIdAndIsDeletedFalseOrderByCreatedDateDesc(boardId);
        }

        return articles.stream()
                .map(ArticleResponse::of)
                .collect(Collectors.toList());
    }

    @Async
    protected CompletableFuture<Article> getArticle(Long boardId, Long articleId) {
        findBoardById(boardId);
        Article article = findArticleById(articleId);
        return CompletableFuture.completedFuture(article);
    }

    @Async
    protected CompletableFuture<List<Comment>> getCommentList(Long articleId) {
        List<Comment> comments = commentRepository.findByArticleIAndId(articleId);
        return CompletableFuture.completedFuture(comments);
    }

    public CompletableFuture<ArticleResponse> getArticleWithComments(Long boardId, Long articleId) {
        CompletableFuture<Article> articleFuture = this.getArticle(boardId, articleId);
        CompletableFuture<List<Comment>> commentListFuture = this.getCommentList(articleId);

        return CompletableFuture.allOf(articleFuture, commentListFuture)
                .thenApply(voidResult -> {
                    Article article = articleFuture.join();
                    List<CommentResponse> comments = commentListFuture.join()
                            .stream().map(CommentResponse::of)
                            .collect(Collectors.toList());
                    return ArticleResponse.of(article, comments);
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    throw new IllegalArgumentException("internal error occurs");
                });
    }

    @Transactional
    public ArticleResponse updateArticle(Long boardId, Long articleId, UpdateArticleRequest articleRequest, UserDetails userDetails) {
        findBoardById(boardId);
        User author = findUserByEmail(userDetails.getUsername());

        validateUpdateRateLimit(author.getEmail());

        Article article = findArticleById(articleId);

        checkArticleOwnership(article, author);
        article.update(articleRequest);
        articleRepository.save(article);
        return ArticleResponse.of(article);
    }

    @Transactional
    public void deleteArticle(Long boardId, Long articleId, UserDetails userDetails) {
        findBoardById(boardId);
        User author = findUserByEmail(userDetails.getUsername());
        Article article = findArticleById(articleId);

        checkArticleOwnership(article, author);
        articleRepository.softDeleteById(articleId);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Article findArticleById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found."));
        if(article.isDeleted()){
            throw new ResourceNotFoundException("Article not found.");
        }
        return article;
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found."));
    }

    private void validateCreateRateLimit(String email) {
        Optional<LocalDateTime> latestArticleDate = articleRepository.findLatestCreatedArticleDate(email);
        if (latestArticleDate.isPresent() && !isAvailable(latestArticleDate.get())) {
            throw new RateLimitException("Articles can only be created once every 5 minutes.");
        }
    }

    private void validateUpdateRateLimit(String email) {
        Optional<LocalDateTime> latestArticleDate = articleRepository.findLatestUpdatedArticleDate(email);
        if (latestArticleDate.isPresent() && !isAvailable(latestArticleDate.get())) {
            throw new RateLimitException("Articles can only be modified once every 5 minutes.");
        }
    }

    private boolean isAvailable(LocalDateTime lastPostingDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastPostingDate, now);
        return duration.toMinutes() >= 5;
    }

    private void checkArticleOwnership(Article article, User author) {
        if (!article.getAuthor().equals(author)) {
            throw new ForbiddenException(String.format("User(id: %d) does not have authority to update Article(id: %d)", author.getId(), article.getId()));
        }
    }
}
