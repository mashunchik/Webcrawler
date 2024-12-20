package com.example.webcrawler_back.proxy;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PageFetcherProxy implements PageFetcher {

    private final PageFetcher pageFetcher;
    private static final long REQUEST_DELAY = 1000; // 1 запит в секунду
    private long lastRequestTime = 0;
    private final Path cacheDir;

    public PageFetcherProxy(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
        this.cacheDir = Paths.get("cache");
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            System.err.println("Could not create cache directory: " + e.getMessage());
        }
    }

    private String getCacheFileName(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes());
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return url.replaceAll("[^a-zA-Z0-9]", "_");
        }
    }

    private Document loadFromCache(String url) {
        Path cachePath = cacheDir.resolve(getCacheFileName(url));
        if (Files.exists(cachePath)) {
            try {
                String html = Files.readString(cachePath);
                System.out.println("Page fetched from cache: " + url);
                return Jsoup.parse(html, url);
            } catch (IOException e) {
                System.err.println("Error reading from cache: " + e.getMessage());
            }
        }
        return null;
    }

    private void saveToCache(String url, Document document) {
        Path cachePath = cacheDir.resolve(getCacheFileName(url));
        try {
            Files.writeString(cachePath, document.outerHtml());
        } catch (IOException e) {
            System.err.println("Error writing to cache: " + e.getMessage());
        }
    }

    @Override
    public Document fetchPage(String url) throws IOException {
        // Спроба завантажити з кешу
        Document cachedDocument = loadFromCache(url);
        if (cachedDocument != null) {
            return cachedDocument;
        }

        // Контроль швидкості
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < REQUEST_DELAY) {
            try {
                Thread.sleep(REQUEST_DELAY - (currentTime - lastRequestTime));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Завантаження сторінки
        Document document = pageFetcher.fetchPage(url);
        saveToCache(url, document);
        lastRequestTime = System.currentTimeMillis();

        System.out.println("Page fetched from web: " + url);
        return document;
    }
}
