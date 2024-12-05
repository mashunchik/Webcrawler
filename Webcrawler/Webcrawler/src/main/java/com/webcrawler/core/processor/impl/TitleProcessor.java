package com.webcrawler.core.processor.impl;

import com.webcrawler.core.processor.BaseProcessor;
import com.webcrawler.core.processor.ProcessingContext;
import org.jsoup.nodes.Document;

public class TitleProcessor extends BaseProcessor {
    @Override
    public void process(Document document, ProcessingContext context) {
        context.setTitle(document.title());
        processNext(document, context);
    }
}
