package com.example.webcrawler_back.services;


import com.example.webcrawler_back.models.Page;
import com.example.webcrawler_back.repositories.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private PageRepository pageRepository;

    @Override
    public Page savePage(Page page) {
        return pageRepository.save(page);
    }

    @Override
    public List<Page> getPagesBySessionId(Integer sessionId) {
        return pageRepository.findAll().stream()
                .filter(page -> page.getSession().getId().equals(sessionId))
                .toList();
    }
}