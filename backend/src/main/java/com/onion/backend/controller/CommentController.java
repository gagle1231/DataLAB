package com.onion.backend.controller;

import com.onion.backend.dto.request.CommentRequest;
import com.onion.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards/{boardId}/articles/{articleId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> addComment(@PathVariable Long boardId,
                                           @PathVariable Long articleId,
                                           @RequestBody CommentRequest commentRequest,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        commentService.writeComment(boardId, articleId, commentRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
