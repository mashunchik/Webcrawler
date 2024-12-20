package com.example.webcrawler_back.proxy;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface PageFetcher {
    Document fetchPage(String url) throws IOException;
}