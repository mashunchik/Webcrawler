package com.example.webcrawler_back.services;

import com.example.webcrawler_back.memento.CrawlerCaretaker;
import com.example.webcrawler_back.memento.CrawlerState;
import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.models.Session;
import com.example.webcrawler_back.models.Statistic;
import com.example.webcrawler_back.models.Term;
import com.example.webcrawler_back.processor.AvailabilityChecker;
import com.example.webcrawler_back.processor.ContentFilterProcessor;
import com.example.webcrawler_back.processor.PageProcessingChain;
import com.example.webcrawler_back.processor.SemanticAnalyzer;
import com.example.webcrawler_back.proxy.PageFetcher;
import com.example.webcrawler_back.proxy.PageFetcherImpl;
import com.example.webcrawler_back.proxy.PageFetcherProxy;
import com.example.webcrawler_back.repositories.PageRepository;
import com.example.webcrawler_back.repositories.SessionRepository;
import com.example.webcrawler_back.repositories.StatisticRepository;
import com.example.webcrawler_back.repositories.TermRepository;
import com.example.webcrawler_back.template.DefaultPageHandler;
import com.example.webcrawler_back.utils.ContentFilter;
import com.example.webcrawler_back.utils.PageNavigator;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the CrawlerService interface.
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {

    private volatile boolean running = false;
    private volatile Queue<String> urlQueue = new LinkedList<>();
    private Set<String> visitedUrls = new HashSet<>();
    private Session currentSession;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private PageFetcher pageFetcher;

    @Autowired
    private PageProcessingChain pageProcessingChain;

    @Autowired
    private ContentFilter contentFilter;

    private DefaultPageHandler pageHandler;
    private final CrawlerCaretaker caretaker = new CrawlerCaretaker(); // Caretaker

    @PostConstruct
    public void init() {
        // Створюємо DefaultPageHandler з усіма необхідними залежностями
        this.pageHandler = new DefaultPageHandler(
            pageFetcher,
            pageProcessingChain,
            pageRepository,
            termRepository,
            statisticRepository,
            contentFilter
        );
    }

    private static final int MAX_METADATA_LENGTH = 255;
    private static final List<String> keywords = Arrays.asList("technology", "data", "machine", "science");

    private void updateStatistics(boolean isSemantic) {
        Statistic statistic = statisticRepository.findTopByOrderByCreatedAtDesc();
        if (statistic == null) {
            statistic = new Statistic();
        }

        statistic.setTotalScans(statistic.getTotalScans() + 1);
        statistic.setPagesVisited(statistic.getPagesVisited() + 1);
        if (isSemantic) {
            statistic.setSemanticPages(statistic.getSemanticPages() + 1);
        }

        statisticRepository.save(statistic);
    }

    @Override
    public void startCrawling(String startingUrl) {
        System.out.println("Starting crawling with URL: " + startingUrl);
        
        // Якщо є активна сесія в статусі PAUSED, відновлюємо її
        if (currentSession != null && "PAUSED".equals(currentSession.getStatus())) {
            System.out.println("Found paused session #" + currentSession.getId() + ", resuming...");
            resumeCrawling();
            return;
        }

        // Інакше починаємо нову сесію
        running = true;
        urlQueue.clear();
        visitedUrls.clear();
        pageHandler.clearCollections();  // Очищаємо колекції в обробнику

        // Create a single session for this crawling operation
        currentSession = new Session();
        currentSession.setStartTime(LocalDateTime.now());
        currentSession.setStatus("IN_PROGRESS");
        sessionRepository.save(currentSession);
        System.out.println("Created new session #" + currentSession.getId());

        urlQueue.add(startingUrl);
        pageHandler.setUrlQueue(urlQueue);  // Синхронізуємо чергу URL
        new Thread(() -> crawl()).start();
    }

    @Override
    public void pauseCrawling() {
        System.out.println("Attempting to pause crawling...");
        if (currentSession != null) {
            System.out.println("Current session #" + currentSession.getId() + " status: " + currentSession.getStatus());
            if ("IN_PROGRESS".equals(currentSession.getStatus())) {
                running = false;
                
                // Оновлюємо локальні колекції з обробника
                urlQueue = new LinkedList<>(pageHandler.getUrlQueue());
                visitedUrls = new HashSet<>(pageHandler.getVisitedUrls());
                
                // Зберігаємо поточний стан
                caretaker.saveState(createMemento());
                System.out.println("Saved crawling state with " + visitedUrls.size() + 
                    " visited URLs and " + urlQueue.size() + " URLs in queue");

                currentSession.setStatus("PAUSED");
                sessionRepository.save(currentSession);
                System.out.println("Session #" + currentSession.getId() + " paused successfully");
            } else {
                System.out.println("Cannot pause: session is not in progress (status: " + currentSession.getStatus() + ")");
            }
        } else {
            System.out.println("Cannot pause: no active session");
        }
    }

    @Override
    public void resumeCrawling() {
        System.out.println("Attempting to resume crawling...");
        if (currentSession != null) {
            System.out.println("Current session #" + currentSession.getId() + " status: " + currentSession.getStatus());
            if ("PAUSED".equals(currentSession.getStatus())) {
                // Відновлюємо стан
                if (caretaker.hasSavedState()) {
                    CrawlerState savedState = caretaker.restoreState();
                    if (savedState.getSessionId().equals(currentSession.getId().toString())) {
                        restoreFromMemento(savedState);
                        
                        // Синхронізуємо стан з обробником
                        pageHandler.setUrlQueue(urlQueue);
                        pageHandler.setVisitedUrls(visitedUrls);
                        
                        System.out.println("Restored previous crawling state with " + 
                            visitedUrls.size() + " visited URLs and " +
                            urlQueue.size() + " URLs in queue");

                        running = true;
                        currentSession.setStatus("IN_PROGRESS");
                        sessionRepository.save(currentSession);
                        System.out.println("Session #" + currentSession.getId() + " resumed successfully");
                        new Thread(() -> crawl()).start();
                    } else {
                        System.out.println("Cannot resume: saved state is for different session");
                    }
                } else {
                    System.out.println("Cannot resume: no saved state found");
                }
            } else {
                System.out.println("Cannot resume: session is not paused (status: " + currentSession.getStatus() + ")");
            }
        } else {
            System.out.println("Cannot resume: no active session");
        }
    }

    @Override
    public void stopCrawling() {
        System.out.println("Attempting to stop crawling...");
        if (currentSession != null) {
            System.out.println("Current session #" + currentSession.getId() + " status: " + currentSession.getStatus());
            running = false;
            urlQueue.clear();
            visitedUrls.clear();
            pageHandler.clearCollections();  // Очищаємо колекції в обробнику
            caretaker.clearHistory();

            currentSession.setEndTime(LocalDateTime.now());
            currentSession.setStatus("COMPLETED");
            sessionRepository.save(currentSession);
            System.out.println("Session #" + currentSession.getId() + " completed successfully");
            currentSession = null;
        } else {
            System.out.println("Cannot stop: no active session");
        }
    }

    private void crawl() {
        try {
            while (running && !urlQueue.isEmpty()) {
                String currentUrl = urlQueue.poll();
                pageHandler.setUrlQueue(urlQueue);  // Синхронізуємо чергу URL

                if (visitedUrls.contains(currentUrl)) {
                    continue;
                }

                try {
                    // Використовуємо шаблонний метод для обробки сторінки
                    pageHandler.handlePage(currentUrl, currentSession);
                    visitedUrls.add(currentUrl);
                    pageHandler.addVisitedUrl(currentUrl);  // Додаємо URL до відвіданих в обробнику
                    
                    // Оновлюємо чергу URL з обробника
                    urlQueue = new LinkedList<>(pageHandler.getUrlQueue());
                    
                    // Зберігаємо проміжний стан кожні 10 URL
                    if (visitedUrls.size() % 10 == 0) {
                        CrawlerState state = new CrawlerState(
                            new HashSet<>(visitedUrls),
                            new LinkedList<>(urlQueue),
                            currentSession.getId().toString()
                        );
                        caretaker.saveState(state);
                        System.out.println("Saved intermediate state with " + 
                            visitedUrls.size() + " visited URLs and " +
                            urlQueue.size() + " URLs in queue");
                    }

                } catch (Exception e) {
                    System.err.println("Error during navigation to: " + currentUrl);
                    System.err.println("Error details: " + e.getMessage());
                }
            }

            // Перевіряємо чи це була пауза чи завершення
            if (currentSession != null) {
                if (!running && "IN_PROGRESS".equals(currentSession.getStatus())) {
                    // Це пауза - зберігаємо стан
                    CrawlerState state = new CrawlerState(
                        new HashSet<>(visitedUrls),
                        new LinkedList<>(urlQueue),
                        currentSession.getId().toString()
                    );
                    caretaker.saveState(state);
                    currentSession.setStatus("PAUSED");
                    sessionRepository.save(currentSession);
                    System.out.println("Crawling paused with " + visitedUrls.size() + 
                        " visited URLs and " + urlQueue.size() + " URLs in queue");
                } else if (urlQueue.isEmpty()) {
                    // Це природне завершення - всі URL оброблені
                    currentSession.setEndTime(LocalDateTime.now());
                    currentSession.setStatus("COMPLETED");
                    sessionRepository.save(currentSession);
                    System.out.println("Crawling completed naturally with " + 
                        visitedUrls.size() + " total URLs processed");
                }
            }

        } catch (Exception e) {
            if (currentSession != null) {
                currentSession.setEndTime(LocalDateTime.now());
                currentSession.setStatus("ERROR");
                sessionRepository.save(currentSession);
            }
            e.printStackTrace();
        }
    }

    private String truncateMetadata(String metadata) {
        if (metadata.length() > MAX_METADATA_LENGTH) {
            return metadata.substring(0, MAX_METADATA_LENGTH);
        }
        return metadata;
    }

    /**
     * Creates a memento containing the current state of the crawler
     */
    private CrawlerState createMemento() {
        return new CrawlerState(
            visitedUrls,
            urlQueue,
            currentSession.getId().toString()
        );
    }

    /**
     * Restores the crawler state from a memento
     */
    private void restoreFromMemento(CrawlerState memento) {
        visitedUrls.clear();
        urlQueue.clear();
        visitedUrls.addAll(memento.getVisitedUrls());
        urlQueue.addAll(memento.getUrlQueue());
    }
}
