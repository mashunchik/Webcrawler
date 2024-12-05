package com.webcrawler.core.processor.impl;

import com.webcrawler.core.processor.BaseProcessor;
import com.webcrawler.core.processor.ProcessingContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkProcessor extends BaseProcessor {
    @Override
    public void process(Document document, ProcessingContext context) {
        String baseUrl = context.getUrl();
        Elements links = document.select("a[href]");
        
        for (Element link : links) {
            String href = link.attr("abs:href").trim();
            if (isValidUrl(href) && isSameDomain(baseUrl, href)) {
                context.addLink(href);
            }
        }
        
        processNext(document, context);
    }
    
    private boolean isValidUrl(String url) {
        try {
            new URI(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    private boolean isSameDomain(String baseUrl, String url) {
        try {
            URI baseUri = new URI(baseUrl);
            URI uri = new URI(url);
            return baseUri.getHost().equalsIgnoreCase(uri.getHost());
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
