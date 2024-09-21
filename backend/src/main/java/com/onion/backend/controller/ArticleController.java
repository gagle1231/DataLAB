package com.onion.backend.controller;

import com.onion.backend.dto.request.UpdateArticleRequest;
import com.onion.backend.dto.request.WriteArticleRequest;
import com.onion.backend.dto.response.ArticleResponse;
import com.onion.backend.entity.Article;
import com.onion.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class ArticleController {

    private final AuthenticationManager authenticationManager;
    private final ArticleService articleService;

    @PostMapping("/{boardId}/articles")
    public ResponseEntity<ArticleResponse> getArticles(@PathVariable Long boardId,
                                                       @RequestBody WriteArticleRequest articleRequest,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        ArticleResponse article = articleService.writeArticle(boardId, articleRequest, userDetails);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/{boardId}/articles")
    public ResponseEntity<List<ArticleResponse>> getArticles(@PathVariable Long boardId,
                                                             @RequestParam(required = false) Long firstId,
                                                             @RequestParam(required = false) Long lastId) {
        List<ArticleResponse> articleResponseList = articleService.getTopArticles(boardId, firstId, lastId);
        return ResponseEntity.ok(articleResponseList);
    }

    @GetMapping("/{boardId}/articles/{articleId}")
    public ResponseEntity<ArticleResponse> getArticles(@PathVariable Long boardId, @PathVariable Long articleId) {
        ArticleResponse articleResponse = articleService.getArticle(boardId, articleId);
        return ResponseEntity.ok(articleResponse);
    }

    @PutMapping("/{boardId}/articles/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long boardId,
                                                         @PathVariable Long articleId,
                                                         @RequestBody UpdateArticleRequest articleRequest,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        ArticleResponse articleResponse = articleService.updateArticle(boardId, articleId, articleRequest, userDetails);
        return ResponseEntity.ok(articleResponse);
    }
}
