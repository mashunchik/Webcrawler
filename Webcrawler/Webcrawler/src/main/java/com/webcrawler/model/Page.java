package com.webcrawler.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Page {
    private Long id;
    private String url;
    private String title;
    private String content;
    private LocalDateTime crawledAt;
    private Set<String> links;
    private String metadata;

    public Page() {
        this.links = new HashSet<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCrawledAt() { return crawledAt; }
    public void setCrawledAt(LocalDateTime crawledAt) { this.crawledAt = crawledAt; }
    
    public Set<String> getLinks() { return links; }
    public void setLinks(Set<String> links) { this.links = links; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
