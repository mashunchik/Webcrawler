package com.webcrawler.core.proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseHttpClient implements IHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(BaseHttpClient.class);
    protected static final int TIMEOUT_MS = 10000;
    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    @Override
    public Document fetch(String url) throws IOException {
        logger.debug("Fetching URL: {}", url);
        return Jsoup.connect(url)
                .timeout(TIMEOUT_MS)
                .userAgent(USER_AGENT)
                .get();
    }

    @Override
    public boolean isAllowed(String url) {
        // Base implementation always allows
        return true;
    }

    @Override
    public void clearCache() {
        // No caching in base implementation
    }

    protected String getDomainName(String url) throws IOException {
        URL urlObj = new URL(url);
        String domain = urlObj.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
