package com.onion.backend.entity;

import com.onion.backend.dto.request.UpdateArticleRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User author;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Board board;

    @Lob
    @Column(nullable = false)
    private String content;

    @Builder
    public Article(String title, User author, Board board, String content) {
        this.title = title;
        this.author = author;
        this.board = board;
        this.content = content;
    }

    public void update(UpdateArticleRequest articleRequest) {
        articleRequest.title().ifPresent(newTitle -> this.title = newTitle);
        articleRequest.content().ifPresent(newContent -> this.content = newContent);
    }
}
