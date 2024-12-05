package com.webcrawler.core.proxy;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.net.HttpURLConnection;

// Proxy Pattern implementation
public class HttpProxy implements IHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpProxy.class);
    private final IHttpClient httpClient;
    private final Map<String, Document> cache;
    private final Map<String, Long> lastAccessTime;
    private final Map<String, SimpleRobotRules> robotsCache;
    private final Map<String, Long> domainAccessTimes;
    
    private static final long CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(1);
    private static final long RATE_LIMIT_MS = TimeUnit.SECONDS.toMillis(1); // 1 second between requests to same domain

    public HttpProxy() {
        this.httpClient = new BaseHttpClient();
        this.cache = new ConcurrentHashMap<>();
        this.lastAccessTime = new ConcurrentHashMap<>();
        this.robotsCache = new ConcurrentHashMap<>();
        this.domainAccessTimes = new ConcurrentHashMap<>();
    }

    @Override
    public Document fetch(String url) throws IOException {
        logger.debug("Proxy receiving request for URL: {}", url);
        
        // Check if crawling is allowed
        if (!isAllowed(url)) {
            throw new IOException("Crawling not allowed by robots.txt: " + url);
        }

        // Apply rate limiting
        applyRateLimiting(url);

        // Check cache
        if (cache.containsKey(url)) {
            long lastAccess = lastAccessTime.get(url);
            if (System.currentTimeMillis() - lastAccess < CACHE_DURATION_MS) {
                logger.debug("Cache hit for URL: {}", url);
                return cache.get(url);
            } else {
                logger.debug("Cache expired for URL: {}", url);
                cache.remove(url);
                lastAccessTime.remove(url);
            }
        }

        try {
            // Fetch from network
            Document doc = httpClient.fetch(url);

            // Cache the result
            cache.put(url, doc);
            lastAccessTime.put(url, System.currentTimeMillis());

            return doc;
        } catch (IOException e) {
            logger.error("Error fetching URL: {}", url, e);
            throw e;
        }
    }

    @Override
    public boolean isAllowed(String url) {
        try {
            String domain = getDomainName(url);
            SimpleRobotRules rules = getRobotRules(domain);
            return rules.isAllowed(url);
        } catch (IOException e) {
            logger.error("Error checking robots.txt for URL: {}", url, e);
            return false;
        }
    }

    @Override
    public void clearCache() {
        cache.clear();
        lastAccessTime.clear();
        robotsCache.clear();
        domainAccessTimes.clear();
    }

    private SimpleRobotRules getRobotRules(String domain) throws IOException {
        if (robotsCache.containsKey(domain)) {
            return robotsCache.get(domain);
        }

        try {
            URL robotsUrl = new URL("https://" + domain + "/robots.txt");
            HttpURLConnection conn = (HttpURLConnection) robotsUrl.openConnection();
            conn.setRequestProperty("User-Agent", BaseHttpClient.USER_AGENT);

            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            SimpleRobotRules rules;

            if (conn.getResponseCode() == 200) {
                byte[] content = conn.getInputStream().readAllBytes();
                rules = parser.parseContent(robotsUrl.toString(), content, "text/plain", BaseHttpClient.USER_AGENT);
            } else {
                // If no robots.txt, allow all
                rules = parser.parseContent(robotsUrl.toString(), new byte[0], "text/plain", BaseHttpClient.USER_AGENT);
            }

            robotsCache.put(domain, rules);
            return rules;
        } catch (IOException e) {
            logger.warn("Error fetching robots.txt for domain: {}. Defaulting to allow all.", domain);
            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            SimpleRobotRules rules = parser.parseContent("", new byte[0], "text/plain", BaseHttpClient.USER_AGENT);
            robotsCache.put(domain, rules);
            return rules;
        }
    }

    private String getDomainName(String url) throws IOException {
        URL urlObj = new URL(url);
        String domain = urlObj.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    private void applyRateLimiting(String url) throws IOException {
        String domain = getDomainName(url);
        Long lastAccess = domainAccessTimes.get(domain);
        
        if (lastAccess != null) {
            long waitTime = RATE_LIMIT_MS - (System.currentTimeMillis() - lastAccess);
            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Rate limiting interrupted", e);
                }
            }
        }
        
        domainAccessTimes.put(domain, System.currentTimeMillis());
    }
}
