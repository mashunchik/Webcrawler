package com.example.webcrawler_back.repositories;

import com.example.webcrawler_back.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Session findTopByOrderByStartTimeDesc();
    List<Session> findTop10ByOrderByStartTimeDesc();
}