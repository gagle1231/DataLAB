package com.onion.backend.dto.request;

import java.util.Optional;

public record UpdateArticleRequest(
        Optional<String> title,
        Optional<String> content
) {
}
