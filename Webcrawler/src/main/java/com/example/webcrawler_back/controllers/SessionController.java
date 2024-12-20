package com.example.webcrawler_back.controllers;


import com.example.webcrawler_back.models.Session;
import com.example.webcrawler_back.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public Session createSession(@RequestBody Session session) {
        return sessionService.createSession(session);
    }

    @GetMapping
    public List<Session> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/{id}")
    public Session getSessionById(@PathVariable Integer id) {
        return sessionService.getSessionById(id);
    }
}