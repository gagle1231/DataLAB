package com.onion.backend.controller;

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
        ArticleResponse article = articleService.writeArticle(articleRequest, userDetails);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/{boardId}/articles/{articleId}")
    public ResponseEntity<Void> getArticle(@PathVariable Long boardId, @PathVariable Long articleId) {

    }

}
