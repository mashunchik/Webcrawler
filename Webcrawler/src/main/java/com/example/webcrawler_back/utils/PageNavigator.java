package com.example.webcrawler_back.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PageNavigator {

    public Set<String> extractLinks(String url) {
        Set<String> links = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).get();
            for (Element link : doc.select("a[href]")) {
                String href = link.attr("abs:href");
                if (href != null && !href.isEmpty()) {
                    links.add(href);
                }
            }
        } catch (IOException e) {
            System.err.println("Error fetching the URL: " + e.getMessage());
        }
        return links;
    }
}