package com.example.webcrawler_back.memento;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CrawlerState implements Serializable {
    private final Set<String> visitedUrls;
    private final Queue<String> urlQueue;
    private final String sessionId;

    public CrawlerState(Set<String> visitedUrls, Queue<String> urlQueue, String sessionId) {
        // Create defensive copies of collections
        this.visitedUrls = new HashSet<>(visitedUrls);
        this.urlQueue = new LinkedList<>(urlQueue);
        this.sessionId = sessionId;
    }

    public Set<String> getVisitedUrls() {
        return new HashSet<>(visitedUrls);
    }

    public Queue<String> getUrlQueue() {
        return new LinkedList<>(urlQueue);
    }

    public String getSessionId() {
        return sessionId;
    }
}
