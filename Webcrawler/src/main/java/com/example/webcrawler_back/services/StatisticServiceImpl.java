package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Statistic;
import com.example.webcrawler_back.repositories.StatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Override
    public Statistic getStatistics() {
        return statisticRepository.findAll().stream().findFirst().orElse(null);
    }

    @Override
    public void updateStatistics(int totalScans, int pagesVisited, int semanticPages) {
        Statistic statistic = getStatistics();
        if (statistic != null) {
            statistic.setTotalScans(totalScans);
            statistic.setPagesVisited(pagesVisited);
            statistic.setSemanticPages(semanticPages);
            statisticRepository.save(statistic);
        }
    }
}