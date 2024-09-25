package com.onion.backend.repository;

import com.onion.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c.createdDate from Comment c where c.author.email = :email and c.isDeleted = false order by c.createdDate desc limit 1")
    Optional<LocalDateTime> findLatestCommentByAuthor(@Param("email") String email);

    @Query("select c from Comment c where c.article.id = :articleId and c.isDeleted = false ")
    List<Comment> findByArticleIAndId(@Param("articleId") Long articleId);
}
