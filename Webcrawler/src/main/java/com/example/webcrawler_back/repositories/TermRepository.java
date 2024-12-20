package com.example.webcrawler_back.repositories;

import com.example.webcrawler_back.models.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Integer> {
    List<Term> findByTerm(String term);
    List<Term> findByTermContainingIgnoreCase(String term);
}