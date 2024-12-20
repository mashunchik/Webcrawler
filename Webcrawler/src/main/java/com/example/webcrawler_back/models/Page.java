package com.example.webcrawler_back.models;

import jakarta.persistence.*;

@Entity
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "url", nullable = true)
    private String url;

    @Column(name = "is_semantic", nullable = false)
    private Boolean isSemantic;

    @Column(name = "metadata")
    private String metadata;

    public Page(String url, String metadata) {
        this.url = url;
        this.metadata = metadata;
    }
    public Page() {}

    // Getters and Setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getSemantic() {
        return isSemantic;
    }

    public void setSemantic(Boolean semantic) {
        isSemantic = semantic;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}