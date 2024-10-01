package com.onion.backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User author;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Article article;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder
    public Comment(String content, User author, Article article) {
        this.content = content;
        this.author = author;
        this.article = article;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void sectContent(String content) {
        this.content = content;
    }
}
