package com.example.webcrawler_back.processor;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class PageProcessingChain {

    private final List<PageProcessor> processors = new ArrayList<>();

    public void addProcessor(PageProcessor processor) {
        processors.add(processor);
    }

    public void process(Document page) throws Exception {
        for (PageProcessor processor : processors) {
            processor.process(page); // Передає сторінку кожному процесору
        }
    }
}