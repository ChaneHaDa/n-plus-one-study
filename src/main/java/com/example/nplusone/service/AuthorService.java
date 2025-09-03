package com.example.nplusone.service;

import com.example.nplusone.entity.Author;
import com.example.nplusone.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {
    
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public List<Author> findAllAuthorsWithNPlusOneProblem() {
        log.info("=== N+1 문제가 발생하는 메서드 실행 시작 ===");
        
        List<Author> authors = authorRepository.findAll();
        
        log.info("조회된 작가 수: {}", authors.size());
        
        for (Author author : authors) {
            log.info("작가: {} - 책 개수: {}", author.getName(), author.getBooks().size());
        }
        
        log.info("=== N+1 문제가 발생하는 메서드 실행 종료 ===");
        return authors;
    }

    @Transactional(readOnly = true)
    public List<Author> findAllAuthorsWithoutNPlusOneProblem() {
        log.info("=== N+1 문제를 해결한 메서드 실행 시작 ===");
        
        List<Author> authors = authorRepository.findAllWithBooks();
        
        log.info("조회된 작가 수: {}", authors.size());
        
        for (Author author : authors) {
            log.info("작가: {} - 책 개수: {}", author.getName(), author.getBooks().size());
        }
        
        log.info("=== N+1 문제를 해결한 메서드 실행 종료 ===");
        return authors;
    }

    @Transactional(readOnly = true)
    public List<Author> findAllAuthorsWithEntityGraph() {
        log.info("=== EntityGraph를 사용한 N+1 문제 해결 메서드 실행 시작 ===");
        
        List<Author> authors = authorRepository.findBy();
        
        log.info("조회된 작가 수: {}", authors.size());
        
        for (Author author : authors) {
            log.info("작가: {} - 책 개수: {}", author.getName(), author.getBooks().size());
        }
        
        log.info("=== EntityGraph를 사용한 N+1 문제 해결 메서드 실행 종료 ===");
        return authors;
    }

    @Transactional(readOnly = true)
    public List<Author> findAllAuthorsWithJpql() {
        log.info("=== JPQL LEFT JOIN FETCH를 사용한 N+1 문제 해결 메서드 실행 시작 ===");
        
        List<Author> authors = authorRepository.findAllWithJpql();
        
        log.info("조회된 작가 수: {}", authors.size());
        
        for (Author author : authors) {
            log.info("작가: {} - 책 개수: {}", author.getName(), author.getBooks().size());
        }
        
        log.info("=== JPQL LEFT JOIN FETCH를 사용한 N+1 문제 해결 메서드 실행 종료 ===");
        return authors;
    }
}