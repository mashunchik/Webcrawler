package com.example.webcrawler_back.config;

import com.example.webcrawler_back.processor.AvailabilityChecker;
import com.example.webcrawler_back.processor.ContentFilterProcessor;
import com.example.webcrawler_back.processor.PageProcessingChain;
import com.example.webcrawler_back.processor.SemanticAnalyzer;
import com.example.webcrawler_back.proxy.PageFetcher;
import com.example.webcrawler_back.proxy.PageFetcherImpl;
import com.example.webcrawler_back.proxy.PageFetcherProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlerConfig {

    @Bean
    public PageFetcher pageFetcher() {
        return new PageFetcherProxy(new PageFetcherImpl());
    }

    @Bean
    public PageProcessingChain pageProcessingChain() {
        PageProcessingChain chain = new PageProcessingChain();
        chain.addProcessor(new AvailabilityChecker());
        chain.addProcessor(new ContentFilterProcessor());
        chain.addProcessor(new SemanticAnalyzer());
        return chain;
    }
}
