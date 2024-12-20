package com.example.webcrawler_back.template;

import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.models.Session;
import com.example.webcrawler_back.models.Term;
import com.example.webcrawler_back.processor.PageProcessingChain;
import com.example.webcrawler_back.proxy.PageFetcher;
import com.example.webcrawler_back.repositories.PageRepository;
import com.example.webcrawler_back.repositories.StatisticRepository;
import com.example.webcrawler_back.repositories.TermRepository;
import com.example.webcrawler_back.utils.ContentFilter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Конкретна реалізація обробника сторінок.
 * Реалізує всі абстрактні методи з AbstractPageHandler.
 */
public class DefaultPageHandler extends AbstractPageHandler {
    private static final int MAX_METADATA_LENGTH = 255;
    private static final List<String> keywords = Arrays.asList("technology", "data", "machine", "science");
    private final Set<String> visitedUrls = new HashSet<>();
    private final Queue<String> urlQueue = new LinkedList<>();
    private final PageRepository pageRepository;
    private final TermRepository termRepository;
    private final StatisticRepository statisticRepository;
    private final ContentFilter contentFilter;
    public DefaultPageHandler(
            PageFetcher pageFetcher,
            PageProcessingChain pageProcessingChain,
            PageRepository pageRepository,
            TermRepository termRepository,
            StatisticRepository statisticRepository,
            ContentFilter contentFilter) {
        super(pageFetcher, pageProcessingChain);  // Викликаємо конструктор батьківського класу
        this.pageRepository = pageRepository;
        this.termRepository = termRepository;
        this.statisticRepository = statisticRepository;
        this.contentFilter = contentFilter;
    }

    @Override
    protected boolean isRelevantPage(Document doc) {
        return contentFilter.isSemanticContent(doc);
    }
    @Override
    protected Page savePage(String url, Document doc, Session session) {
        String truncatedMetadata = truncateMetadata(doc.title());
        
        Page page = new Page();
        page.setUrl(url);
        page.setSemantic(true);
        page.setMetadata(truncatedMetadata);
        page.setSession(session);
        
        return pageRepository.save(page);
    }
    @Override
    protected void processTerms(Document doc, Page page) {
        String pageText = contentFilter.extractText(doc);
        List<String> foundTerms = contentFilter.extractTerms(doc, keywords);
        
        for (String term : foundTerms) {
            Term termEntity = new Term();
            termEntity.setSession(page.getSession());
            termEntity.setTerm(term);
            termEntity.setPage(page);
            termEntity.setOccurrences(Collections.frequency(Arrays.asList(pageText.split(" ")), term));
            termEntity.setSemantic(true);
            termRepository.save(termEntity);
        }
    }

    @Override
    protected void updateStatistics(Page page) {
        com.example.webcrawler_back.models.Statistic statistic = 
            statisticRepository.findTopByOrderByCreatedAtDesc();
        if (statistic == null) {
            statistic = new com.example.webcrawler_back.models.Statistic();
        }

        statistic.setTotalScans(statistic.getTotalScans() + 1);
        statistic.setPagesVisited(statistic.getPagesVisited() + 1);
        if (page.getSemantic()) {
            statistic.setSemanticPages(statistic.getSemanticPages() + 1);
        }

        statisticRepository.save(statistic);
    }

    @Override
    protected void collectNewUrls(Document doc) {
        for (Element link : doc.select("a[href]")) {
            String newUrl = link.absUrl("href");
            if (newUrl != null && !newUrl.isEmpty() && !visitedUrls.contains(newUrl)) {
                urlQueue.add(newUrl);
            }
        }
    }

    @Override
    protected void handleError(Exception e) {
        System.err.println("Error during page processing: " + e.getMessage());
        e.printStackTrace();
    }
    private String truncateMetadata(String metadata) {
        if (metadata.length() > MAX_METADATA_LENGTH) {
            return metadata.substring(0, MAX_METADATA_LENGTH);
        }
        return metadata;
    }
    public Queue<String> getUrlQueue() {
        return urlQueue;
    }
    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
    public void setUrlQueue(Queue<String> queue) {
        this.urlQueue.clear();
        this.urlQueue.addAll(queue);
    }
    public void setVisitedUrls(Set<String> urls) {
        this.visitedUrls.clear();
        this.visitedUrls.addAll(urls);
    }
    public void addVisitedUrl(String url) {
        this.visitedUrls.add(url);
    }
    public void clearCollections() {
        this.urlQueue.clear();
        this.visitedUrls.clear();
    }
}
