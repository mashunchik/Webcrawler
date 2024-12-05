package com.webcrawler;

import com.webcrawler.core.WebCrawler;
import com.webcrawler.repository.PageRepository;

public class Main {
    public static void main(String[] args) {
        // Initialize repository
        PageRepository repository = new PageRepository();
        
        // Create crawler with configuration
        WebCrawler crawler = new WebCrawler(
            repository,
            5,      // maxDepth
            1000,   // maxPages
            4       // numThreads
        );
        
        // Start crawling from a URL
        crawler.crawl("https://google.com");
    }
}
