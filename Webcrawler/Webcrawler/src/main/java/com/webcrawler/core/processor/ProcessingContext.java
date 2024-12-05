package com.webcrawler.core.processor;

import com.webcrawler.model.Page;
import java.util.HashSet;
import java.util.Set;

public class ProcessingContext {
    private final String url;
    private String title;
    private String content;
    private final Set<String> links;
    private String metadata;

    public ProcessingContext(String url) {
        this.url = url;
        this.links = new HashSet<>();
    }

    public Page toPage() {
        Page page = new Page();
        page.setUrl(url);
        page.setTitle(title);
        page.setContent(content);
        page.setLinks(links);
        page.setMetadata(metadata);
        return page;
    }

    // Getters and Setters
    public String getUrl() { return url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Set<String> getLinks() { return links; }
    public void addLink(String link) { this.links.add(link); }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
