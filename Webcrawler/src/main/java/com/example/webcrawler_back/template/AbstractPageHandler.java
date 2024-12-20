package com.example.webcrawler_back.template;

import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.models.Session;
import com.example.webcrawler_back.processor.PageProcessingChain;
import com.example.webcrawler_back.proxy.PageFetcher;
import org.jsoup.nodes.Document;

/**
 * Абстрактний клас, що реалізує шаблонний метод для обробки веб-сторінок.
 * Визначає загальний алгоритм обробки сторінки, дозволяючи підкласам
 * перевизначати конкретні кроки цього алгоритму.
 */
public abstract class AbstractPageHandler {
    protected final PageFetcher pageFetcher;
    protected final PageProcessingChain pageProcessingChain;
    public AbstractPageHandler(PageFetcher pageFetcher, PageProcessingChain pageProcessingChain) {
        this.pageFetcher = pageFetcher;
        this.pageProcessingChain = pageProcessingChain;
    }
    public final void handlePage(String url, Session session) {
        try {
            Document doc = fetchPage(url);
            if (doc != null) {
                processPageContent(doc);
                if (isRelevantPage(doc)) {
                    Page page = savePage(url, doc, session);
                    processTerms(doc, page);
                    updateStatistics(page);
                    collectNewUrls(doc);
                }
            }
        } catch (Exception e) {
            handleError(e);
        }
    }
    protected Document fetchPage(String url) throws Exception {
        return pageFetcher.fetchPage(url);
    }
    protected void processPageContent(Document doc) throws Exception {
        pageProcessingChain.process(doc);
    }
    protected abstract boolean isRelevantPage(Document doc);
    protected abstract Page savePage(String url, Document doc, Session session);
    protected abstract void processTerms(Document doc, Page page);
    protected abstract void updateStatistics(Page page);
    protected abstract void collectNewUrls(Document doc);
    protected abstract void handleError(Exception e);
}
