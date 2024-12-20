package com.example.webcrawler_back.controllers;

import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.repositories.PageRepository;
import com.example.webcrawler_back.services.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;





    @GetMapping("/start")
    public String startCrawling(@RequestParam String url) {
        try {
            crawlerService.startCrawling(url);
            return "Crawling started at: " + url;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start crawling.";
        }
    }

    @GetMapping("/pause")
    public String pauseCrawling() {
        try {
            crawlerService.pauseCrawling();
            return "Crawling has been paused.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to pause crawling.";
        }
    }

    @GetMapping("/stop")
    public String stopCrawling() {
        try {
            crawlerService.stopCrawling();
            return "Crawling has been completely stopped.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to stop crawling.";
        }
    }

    @GetMapping("/resume")
    public String resumeCrawling() {
        try {
            crawlerService.resumeCrawling();
            return "Crawling has been resumed.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to resume crawling.";
        }
    }
}