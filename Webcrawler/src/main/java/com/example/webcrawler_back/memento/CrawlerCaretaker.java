package com.example.webcrawler_back.memento;

import java.util.Stack;

/**
 * Caretaker class in the Memento pattern.
 * Responsible for keeping track of multiple states of the crawler.
 */
public class CrawlerCaretaker {
    private final Stack<CrawlerState> stateHistory = new Stack<>();

    public void saveState(CrawlerState state) {
        stateHistory.push(state);
    }

    public CrawlerState restoreState() {
        if (!stateHistory.isEmpty()) {
            return stateHistory.pop();
        }
        return null;
    }

    public boolean hasSavedState() {
        return !stateHistory.isEmpty();
    }
    public void clearHistory() {
        stateHistory.clear();
    }
}
