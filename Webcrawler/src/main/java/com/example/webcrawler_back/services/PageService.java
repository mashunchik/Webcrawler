package com.example.webcrawler_back.services;

import com.example.webcrawler_back.models.Page;

import java.util.List;

public interface PageService {
    Page savePage(Page page);
    List<Page> getPagesBySessionId(Integer sessionId);
}