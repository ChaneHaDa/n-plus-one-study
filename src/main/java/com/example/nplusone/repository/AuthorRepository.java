package com.example.nplusone.repository;

import com.example.nplusone.entity.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @Query("SELECT DISTINCT a FROM Author a JOIN FETCH a.books")
    List<Author> findAllWithBooks();
    
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books")
    List<Author> findAllWithJpql();
    
    @EntityGraph(attributePaths = {"books"})
    List<Author> findBy();
}