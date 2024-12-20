package com.example.webcrawler_back.proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PageFetcherImpl implements PageFetcher {

    @Override
    public Document fetchPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}