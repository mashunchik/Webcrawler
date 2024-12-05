package com.webcrawler.core.processor;

import org.jsoup.nodes.Document;

public abstract class BaseProcessor implements PageProcessor {
    protected PageProcessor nextProcessor;

    @Override
    public void setNext(PageProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    protected void processNext(Document document, ProcessingContext context) {
        if (nextProcessor != null) {
            nextProcessor.process(document, context);
        }
    }
}
