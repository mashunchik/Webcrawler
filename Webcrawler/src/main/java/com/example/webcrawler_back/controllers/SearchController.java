package com.example.webcrawler_back.controllers;

import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.models.Term;
import com.example.webcrawler_back.repositories.PageRepository;
import com.example.webcrawler_back.repositories.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private TermRepository termRepository;

    @GetMapping("/{term}")
    public ResponseEntity<?> searchTerm(@PathVariable String term) {
        List<Term> terms = termRepository.findByTermContainingIgnoreCase(term);

        if (terms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Термін не знайдено");
        }

        return ResponseEntity.ok(terms);
    }

}