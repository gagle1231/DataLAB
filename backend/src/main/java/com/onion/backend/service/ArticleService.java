package com.onion.backend.service;

import com.onion.backend.dto.request.UpdateArticleRequest;
import com.onion.backend.dto.request.WriteArticleRequest;
import com.onion.backend.dto.response.ArticleResponse;
import com.onion.backend.entity.Article;
import com.onion.backend.entity.Board;
import com.onion.backend.entity.User;
import com.onion.backend.exception.RateLimitException;
import com.onion.backend.exception.ResourceNotFoundException;
import com.onion.backend.repository.ArticleRepository;
import com.onion.backend.repository.BoardRepository;
import com.onion.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

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

        return new ArticleResponse(articleRepository.save(article));
    }

    public List<ArticleResponse> getTopArticles(Long boardId, Long firstId, Long lastId) {
        List<Article> articles;
        if (firstId != null) {
            articles = articleRepository.findTop10ByBoardIdAndIdGreaterThanOrderByIdAsc(boardId, firstId);
        } else if (lastId != null) {
            articles = articleRepository.findTop10ByBoardIdAndIdLessThanOrderByIdDesc(boardId, lastId);
        } else {
            articles = articleRepository.findTop10ByBoardIdOrderByCreatedDateDesc(boardId);
        }

        return articles.stream()
                .map(ArticleResponse::new)
                .collect(Collectors.toList());
    }

    public ArticleResponse getArticle(Long boardId, Long articleId) {
        Article article = findArticleById(articleId);
        return new ArticleResponse(article);
    }

    @Transactional
    public ArticleResponse updateArticle(Long boardId, Long articleId, UpdateArticleRequest articleRequest, UserDetails userDetails) {
        findBoardById(boardId);
        User author = findUserByEmail(userDetails.getUsername());

        validateUpdateRateLimit(author.getEmail());

        Article article = findArticleById(articleId);
        article.update(articleRequest);
        articleRepository.save(article);
        return new ArticleResponse(article);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found."));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found."));
    }

    private void validateCreateRateLimit(String email) {
        Optional<LocalDateTime> latestArticleDate = articleRepository.findLatestCreatedArticleByAuthor(email);
        if (latestArticleDate.isPresent() && !isAvailable(latestArticleDate.get())) {
            throw new RateLimitException("Articles can only be created once every 5 minutes.");
        }
    }

    private void validateUpdateRateLimit(String email) {
        Optional<LocalDateTime> latestArticleDate = articleRepository.findLatestUpdatedArticleByAuthor(email);
        if (latestArticleDate.isPresent() && !isAvailable(latestArticleDate.get())) {
            throw new RateLimitException("Articles can only be modified once every 5 minutes.");
        }
    }

    private boolean isAvailable(LocalDateTime lastPostingDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastPostingDate, now);
        return duration.toMinutes() >= 5;
    }
}
