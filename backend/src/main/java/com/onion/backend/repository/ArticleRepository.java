package com.onion.backend.repository;

import com.onion.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop10ByBoardIdOrderByCreatedDateDesc(Long boardId);
}
