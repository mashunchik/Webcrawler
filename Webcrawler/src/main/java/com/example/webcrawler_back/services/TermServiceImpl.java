package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Term;
import com.example.webcrawler_back.repositories.PageRepository;
import com.example.webcrawler_back.repositories.SessionRepository;
import com.example.webcrawler_back.repositories.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TermServiceImpl implements TermService {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public List<Term> searchTerm(String term) {
        return termRepository.findByTerm(term);
    }
}