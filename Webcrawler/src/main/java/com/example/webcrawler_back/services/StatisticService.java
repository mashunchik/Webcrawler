package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Statistic;

public interface StatisticService {
    Statistic getStatistics();
    void updateStatistics(int totalScans, int pagesVisited, int semanticPages);
}