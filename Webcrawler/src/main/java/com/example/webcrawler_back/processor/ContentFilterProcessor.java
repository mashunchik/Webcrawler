package com.example.webcrawler_back.processor;

import com.example.webcrawler_back.utils.ContentFilter;
import org.jsoup.nodes.Document;

public class ContentFilterProcessor implements PageProcessor {

    private final ContentFilter contentFilter = new ContentFilter();

    @Override
    public void process(Document page) {
        contentFilter.removeNonSemanticContent(page);
        System.out.println("Non-semantic content removed.");
    }
}