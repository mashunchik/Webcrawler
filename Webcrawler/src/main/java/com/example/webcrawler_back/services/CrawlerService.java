package com.example.webcrawler_back.services;

public interface CrawlerService {
    void startCrawling(String startingUrl);
    void pauseCrawling();
    void resumeCrawling();
    void stopCrawling();
}
