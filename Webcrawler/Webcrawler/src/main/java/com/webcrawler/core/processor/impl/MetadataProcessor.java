package com.webcrawler.core.processor.impl;

import com.webcrawler.core.processor.BaseProcessor;
import com.webcrawler.core.processor.ProcessingContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.JSONObject;

public class MetadataProcessor extends BaseProcessor {
    @Override
    public void process(Document document, ProcessingContext context) {
        JSONObject metadata = new JSONObject();
        
        // Extract meta tags
        for (Element meta : document.select("meta")) {
            String name = meta.attr("name");
            String content = meta.attr("content");
            if (!name.isEmpty() && !content.isEmpty()) {
                metadata.put(name, content);
            }
        }
        
        // Extract OpenGraph metadata
        for (Element meta : document.select("meta[property^=og:]")) {
            String property = meta.attr("property");
            String content = meta.attr("content");
            if (!property.isEmpty() && !content.isEmpty()) {
                metadata.put(property, content);
            }
        }
        
        context.setMetadata(metadata.toString());
        processNext(document, context);
    }
}
