package com.example.webcrawler_back.processor;

import org.jsoup.nodes.Document;

public class AvailabilityChecker implements PageProcessor {

    @Override
    public void process(Document page) throws Exception {
        if (page == null) {
            throw new Exception("Page is not accessible or does not exist.");
        }
        System.out.println("Page is available.");
    }
}