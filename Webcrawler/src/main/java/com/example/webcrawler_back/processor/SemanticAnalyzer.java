package com.example.webcrawler_back.processor;

import com.example.webcrawler_back.utils.ContentFilter;
import org.jsoup.nodes.Document;

public class SemanticAnalyzer implements PageProcessor {

    private final ContentFilter contentFilter = new ContentFilter();

    @Override
    public void process(Document page) {
        boolean isSemantic = contentFilter.isSemanticContent(page);
        System.out.println("Semantic analysis completed. Is semantic: " + isSemantic);
    }
}