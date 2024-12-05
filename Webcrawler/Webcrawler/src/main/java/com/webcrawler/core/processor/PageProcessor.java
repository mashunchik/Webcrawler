package com.webcrawler.core.processor;

import org.jsoup.nodes.Document;

// Chain of Responsibility Pattern - Handler interface
public interface PageProcessor {
    void setNext(PageProcessor nextProcessor);
    void process(Document document, ProcessingContext context);
}
