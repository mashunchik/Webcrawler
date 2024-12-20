package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Session;
import com.example.webcrawler_back.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public Session createSession(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Session getSessionById(Integer id) {
        Optional<Session> session = sessionRepository.findById(id);
        return session.orElse(null);
    }
}