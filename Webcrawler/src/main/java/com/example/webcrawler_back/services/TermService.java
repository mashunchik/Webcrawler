package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Term;

import java.util.List;

public interface TermService {
    List<Term> searchTerm(String term);
}
