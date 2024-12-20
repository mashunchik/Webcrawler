package com.example.webcrawler_back.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class FileStorageService {

    private static final String BASE_DIRECTORY = System.getProperty("user.dir") + File.separator + "crawled_pages"; // Базова папка для збереження файлів

    public void savePageAsHtml(String url, String title, String content) throws IOException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        try {
            // Створюємо ім'я файлу з URL
            String sanitizedUrl = url.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String fileName = sanitizedUrl + ".html";

            // Директория для домену
            String domain = extractDomainFromUrl(url);
            if (domain == null || domain.isEmpty()) {
                throw new IllegalArgumentException("Could not extract domain from URL: " + url);
            }

            File directory = new File(BASE_DIRECTORY + File.separator + domain);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }

            // Повний шлях до файлу
            File file = new File(directory, fileName);

            // Генерація HTML-контенту
            String htmlContent = generateHtmlContent(title, content);

            // Записуємо у файл
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(htmlContent);
            }

            System.out.println("Page saved as HTML: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to save page as HTML: " + e.getMessage());
            throw e;
        }
    }

    private String generateHtmlContent(String title, String content) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>" + title + "</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>" + title + "</h1>\n" +
                "    <div>" + content + "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String extractDomainFromUrl(String url) {
        try {
            return new java.net.URL(url).getHost();
        } catch (Exception e) {
            return "unknown_domain";
        }
    }
}