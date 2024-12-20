package com.example.webcrawler_back.processor;

import org.jsoup.nodes.Document;

public interface PageProcessor {
    void process(Document page) throws Exception;
}