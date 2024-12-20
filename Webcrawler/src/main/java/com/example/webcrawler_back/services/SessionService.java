package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Session;

import java.util.List;

public interface SessionService {
    Session createSession(Session session);
    List<Session> getAllSessions();
    Session getSessionById(Integer id);
}