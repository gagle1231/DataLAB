package com.onion.backend.repository;

import com.onion.backend.entity.Article;
import com.onion.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop10ByBoardIdOrderByCreatedDateDesc(Long boardId);

    @Query("select a from Article a where a.board.id = :boardId and a.id > :firstId order by a.id asc")
    List<Article> findTop10ByBoardIdAndIdGreaterThanOrderByIdAsc(@Param("boardId") Long boardId, @Param("firstId") Long firstId);

    @Query("select a from Article a where a.board.id = :boardId and a.id < :lastId order by a.id desc")
    List<Article> findTop10ByBoardIdAndIdLessThanOrderByIdDesc(@Param("boardId") Long boardId, @Param("lastId") Long lastId);

    @Query("select a.createdDate from Article a where a.author.email = :email order by a.createdDate desc limit 1")
    Optional<LocalDateTime> findLatestCreatedArticleByAuthor(@Param("email") String email);

    @Query("select a.modifiedDate from Article a where a.author.email = :email order by a.modifiedDate desc limit 1")
    Optional<LocalDateTime> findLatestUpdatedArticleByAuthor(@Param("email") String email);
}
