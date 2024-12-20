package com.example.webcrawler_back.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "total_scans", nullable = false)
    private Integer totalScans = 0;

    @Column(name = "pages_visited", nullable = false)
    private Integer pagesVisited = 0;

    @Column(name = "semantic_pages", nullable = false)
    private Integer semanticPages = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(Integer totalScans) {
        this.totalScans = totalScans;
    }

    public Integer getPagesVisited() {
        return pagesVisited;
    }

    public void setPagesVisited(Integer pagesVisited) {
        this.pagesVisited = pagesVisited;
    }

    public Integer getSemanticPages() {
        return semanticPages;
    }

    public void setSemanticPages(Integer semanticPages) {
        this.semanticPages = semanticPages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}