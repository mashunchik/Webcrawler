package com.example.webcrawler_back.repositories;

import com.example.webcrawler_back.models.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    @Query("SELECT p FROM Page p WHERE p.metadata LIKE %:term%")
    List<Page> findByTerm(@Param("term") String term);

}