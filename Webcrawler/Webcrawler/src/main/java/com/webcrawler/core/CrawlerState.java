package com.webcrawler.core;

import java.util.HashSet;
import java.util.Set;

// Memento Pattern - State object
public class CrawlerState {
    private final Set<String> visitedUrls;
    private final Set<String> pendingUrls;
    private final long timestamp;

    public CrawlerState(Set<String> visitedUrls, Set<String> pendingUrls) {
        this.visitedUrls = new HashSet<>(visitedUrls);
        this.pendingUrls = new HashSet<>(pendingUrls);
        this.timestamp = System.currentTimeMillis();
    }

    public Set<String> getVisitedUrls() {
        return new HashSet<>(visitedUrls);
    }

    public Set<String> getPendingUrls() {
        return new HashSet<>(pendingUrls);
    }

    public long getTimestamp() {
        return timestamp;
    }
}
