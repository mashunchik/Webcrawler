package com.example.webcrawler_back.repositories;


import com.example.webcrawler_back.models.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {
    Statistic findTopByOrderByCreatedAtDesc();
}