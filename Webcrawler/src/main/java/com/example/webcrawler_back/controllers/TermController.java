package com.example.webcrawler_back.controllers;

import com.example.webcrawler_back.models.Term;
import com.example.webcrawler_back.services.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
public class TermController {

    @Autowired
    private TermService termService;

    @GetMapping("/search")
    public ResponseEntity<List<Term>> search(@RequestParam String query) {
        List<Term> terms = termService.searchTerm(query);
        return ResponseEntity.ok(terms);
    }
}