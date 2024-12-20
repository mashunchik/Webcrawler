package com.example.webcrawler_back.models;

import jakarta.persistence.*;

@Entity
@Table(name = "terms")
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(name = "term", nullable = false)
    private String term;

    @Column(name = "occurrences")
    private Integer occurrences; // Кількість разів, коли термін зустрічається

    @Column(name = "is_semantic")
    private Boolean isSemantic; // Позначає, чи термін семантичний

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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Integer getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Integer occurrences) {
        this.occurrences = occurrences;
    }

    public Boolean getSemantic() {
        return isSemantic;
    }

    public void setSemantic(Boolean semantic) {
        isSemantic = semantic;
    }
}