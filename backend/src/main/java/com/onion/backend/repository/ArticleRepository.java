package com.onion.backend.repository;

import com.onion.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop10ByBoardIdOrderByCreatedDateDesc(Long boardId);

    @Query("select a from Article a where a.board.id = :boardId and a.id > :firstId order by a.id asc")
    List<Article> findTop10ByBoardIdAndIdGreaterThanOrderByIdAsc(@Param("boardId") Long boardId, @Param("firstId") Long firstId);

    @Query("select a from Article a where a.board.id = :boardId and a.id < :lastId order by a.id desc")
    List<Article> findTop10ByBoardIdAndIdLessThanOrderByIdDesc(@Param("boardId") Long boardId, @Param("lastId") Long lastId);

}
