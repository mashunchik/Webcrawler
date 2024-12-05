package com.webcrawler.core.proxy;

import org.jsoup.nodes.Document;
import java.io.IOException;

public interface IHttpClient {
    Document fetch(String url) throws IOException;
    boolean isAllowed(String url);
    void clearCache();
}
