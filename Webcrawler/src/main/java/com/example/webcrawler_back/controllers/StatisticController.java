package com.example.webcrawler_back.controllers;


import com.example.webcrawler_back.models.Statistic;
import com.example.webcrawler_back.repositories.StatisticRepository;
import com.example.webcrawler_back.services.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    @Autowired
    private StatisticRepository statisticRepository;

    @GetMapping("/")
    public ResponseEntity<?> getStatistics() {
        Statistic statistic = statisticRepository.findTopByOrderByCreatedAtDesc();
        if (statistic != null) {
            return ResponseEntity.ok(statistic);
        }
        return ResponseEntity.ok("Статистика ще не зібрана.");
    }
}