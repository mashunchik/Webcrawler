package com.webcrawler.core.processor.impl;

import com.webcrawler.core.processor.BaseProcessor;
import com.webcrawler.core.processor.ProcessingContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ContentProcessor extends BaseProcessor {
    @Override
    public void process(Document document, ProcessingContext context) {
        // Remove non-semantic elements
        document.select("script, style, iframe, .advertisement").remove();
        
        // Extract main content
        StringBuilder content = new StringBuilder();
        
        // Try to find main content container
        Elements mainContent = document.select("main, article, .content, #content, .post, .entry");
        if (!mainContent.isEmpty()) {
            for (Element element : mainContent) {
                content.append(element.text()).append("\n");
            }
        } else {
            // Fallback to body text if no main content container found
            content.append(document.body().text());
        }
        
        context.setContent(content.toString().trim());
        processNext(document, context);
    }
}
