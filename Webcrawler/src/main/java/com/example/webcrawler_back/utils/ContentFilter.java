package com.example.webcrawler_back.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContentFilter {

    public List<String> extractTerms(Document doc, List<String> keywords) {
        String text = doc.text();
        List<String> foundTerms = new ArrayList<>();

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                foundTerms.add(keyword);
            }
        }
        return foundTerms;
    }

    private static final int MIN_TEXT_LENGTH = 100; // Мінімальна довжина тексту для визнання сторінки семантичною

    // Видаляє несемантичний контент
    public Document removeNonSemanticContent(Document doc) {
        doc.select("script, style, iframe, .advertisement, .popup").remove(); // CSS-селектори для видалення
        return doc;
    }

    // Витягує текст і аналізує, чи сторінка семантична
    public boolean isSemanticContent(Document doc) {
        String text = doc.body().text();
        return text.length() > MIN_TEXT_LENGTH; // Простий критерій — довжина тексту
    }

    // Витягує очищений текст сторінки
    public String extractText(Document doc) {
        return doc.body().text();
    }
}