package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.models.Session;
import com.example.webcrawler_back.repositories.PageRepository;
import com.example.webcrawler_back.repositories.SessionRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class PageProcessorService {

    @Autowired
    private PageRepository pageRepository; // Репозиторій для збереження даних до БД.
    private SessionRepository sessionRepository;
    private final Set<String> visitedUrls = new HashSet<>();

    public void process(String url) {
        navigate(url);
    }

    public void navigate(String url) {
        try {
            if (visitedUrls.contains(url)) {
                return; // Уникати повторного сканування.
            }

            visitedUrls.add(url);

            Document doc = Jsoup.connect(url).get();

            if (isSemanticContent(doc)) {
                savePageData(url, doc.title());
            }

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (isValidURL(nextUrl)) {
                    navigate(nextUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSemanticContent(Document doc) {
        // Перевірка на рекламу та інший непотрібний контент.
        if (doc.select("script").size() > 0) {
            return false;
        }

        if (doc.select(".advert, .ads").size() > 0) {
            return false;
        }

        return true; // Сторінка відповідає умовам.
    }

    public boolean isValidURL(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public void savePageData(String url, String metadata) {
        try {
            Session session = new Session();
            session.setStartTime(LocalDateTime.now());
            session.setStatus("IN_PROGRESS");

            // Збережи Session
            sessionRepository.save(session);


            Page page = new Page();
            page.setSession(session);
            page.setUrl(url);
            page.setSemantic(true);
            page.setMetadata(metadata);

            pageRepository.save(page);
            System.out.println("Saved Page: " + page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}