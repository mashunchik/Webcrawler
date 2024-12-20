package com.example.webcrawler_back.controllers;


import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.services.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private PageService pageService;

    @PostMapping
    public Page savePage(@RequestBody Page page) {
        return pageService.savePage(page);
    }

    @GetMapping("/session/{sessionId}")
    public List<Page> getPagesBySessionId(@PathVariable Integer sessionId) {
        return pageService.getPagesBySessionId(sessionId);
    }
}