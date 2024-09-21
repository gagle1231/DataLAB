package com.onion.backend.repository;

import com.onion.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop10ByBoardIdAndIsDeletedFalseOrderByCreatedDateDesc(Long boardId);

    @Query("select a from Article a where a.board.id = :boardId and a.isDeleted = false and a.id > :firstId order by a.id asc")
    List<Article> findNextArticles(@Param("boardId") Long boardId, @Param("firstId") Long firstId);

    @Query("select a from Article a where a.board.id = :boardId and a.isDeleted = false and a.id < :lastId order by a.id desc")
    List<Article> findPreviousArticles(@Param("boardId") Long boardId, @Param("lastId") Long lastId);

    @Query("select a.createdDate from Article a where a.author.email = :email and a.isDeleted = false order by a.createdDate desc")
    Optional<LocalDateTime> findLatestCreatedArticleDate(@Param("email") String email);

    @Query("select a.modifiedDate from Article a where a.author.email = :email and a.isDeleted = false order by a.modifiedDate desc")
    Optional<LocalDateTime> findLatestUpdatedArticleDate(@Param("email") String email);

    @Modifying
    @Query("UPDATE Article a SET a.isDeleted = true WHERE a.id = :id")
    void softDeleteById(@Param("id") Long id);
}
