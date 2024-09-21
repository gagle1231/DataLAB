package com.onion.backend.service;

import com.onion.backend.dto.request.WriteArticleRequest;
import com.onion.backend.dto.response.ArticleResponse;
import com.onion.backend.entity.Article;
import com.onion.backend.entity.Board;
import com.onion.backend.entity.User;
import com.onion.backend.exception.ResourceNotFoundException;
import com.onion.backend.repository.ArticleRepository;
import com.onion.backend.repository.BoardRepository;
import com.onion.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시판을 찾을 수 없습니다"));

        User author = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow( ()-> {
                    throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
                });

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
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시물을 찾을 수 없습니다"));
        return new ArticleResponse(article);
    }

}
