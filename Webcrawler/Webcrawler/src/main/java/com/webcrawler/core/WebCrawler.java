package com.webcrawler.core;

import com.webcrawler.core.processor.*;
import com.webcrawler.core.processor.impl.*;
import com.webcrawler.core.proxy.HttpProxy;
import com.webcrawler.model.Page;
import com.webcrawler.repository.PageRepository;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
    private final HttpProxy httpProxy;
    private final PageRepository pageRepository;
    private final Set<String> visitedUrls;
    private final Queue<String> pendingUrls;
    private final PageProcessor processingChain;
    private final ExecutorService executorService;
    private final int maxDepth;
    private final int maxPages;

    public WebCrawler(PageRepository pageRepository, int maxDepth, int maxPages, int numThreads) {
        this.httpProxy = new HttpProxy();
        this.pageRepository = pageRepository;
        this.visitedUrls = Collections.synchronizedSet(new HashSet<>());
        this.pendingUrls = new ConcurrentLinkedQueue<>();
        this.maxDepth = maxDepth;
        this.maxPages = maxPages;
        this.executorService = Executors.newFixedThreadPool(numThreads);
        
        // Initialize processing chain (Chain of Responsibility pattern)
        this.processingChain = createProcessingChain();
    }

    private PageProcessor createProcessingChain() {
        PageProcessor titleProcessor = new TitleProcessor();
        PageProcessor contentProcessor = new ContentProcessor();
        PageProcessor linkProcessor = new LinkProcessor();
        PageProcessor metadataProcessor = new MetadataProcessor();

        titleProcessor.setNext(contentProcessor);
        contentProcessor.setNext(linkProcessor);
        linkProcessor.setNext(metadataProcessor);

        return titleProcessor;
    }

    public void crawl(String startUrl) {
        pendingUrls.offer(startUrl);
        
        List<Future<?>> futures = new ArrayList<>();
        while (!pendingUrls.isEmpty() && visitedUrls.size() < maxPages) {
            String url = pendingUrls.poll();
            if (url != null && !visitedUrls.contains(url)) {
                futures.add(executorService.submit(() -> processSingleUrl(url)));
            }
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error waiting for crawl task to complete", e);
            }
        }

        shutdown();
    }

    private void processSingleUrl(String url) {
        if (visitedUrls.contains(url)) {
            return;
        }

        try {
            visitedUrls.add(url);
            Document document = httpProxy.fetch(url);
            
            ProcessingContext context = new ProcessingContext(url);
            processingChain.process(document, context);
            
            Page page = context.toPage();
            pageRepository.save(page);
            
            // Add new URLs to pending queue
            for (String link : context.getLinks()) {
                if (!visitedUrls.contains(link)) {
                    pendingUrls.offer(link);
                }
            }
            
        } catch (IOException e) {
            logger.error("Error processing URL: " + url, e);
        }
    }

    public CrawlerState saveState() {
        return new CrawlerState(visitedUrls, new HashSet<>(pendingUrls));
    }

    public void restoreState(CrawlerState state) {
        visitedUrls.clear();
        visitedUrls.addAll(state.getVisitedUrls());
        
        pendingUrls.clear();
        pendingUrls.addAll(state.getPendingUrls());
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
