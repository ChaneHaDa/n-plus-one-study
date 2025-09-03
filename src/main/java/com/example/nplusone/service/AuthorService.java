package com.example.nplusone.service;

import com.example.nplusone.entity.Author;
import com.example.nplusone.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public List<Author> testInQueryPerformance(int count) {
        log.info("=== IN 쿼리 성능 테스트 시작: {} 개의 ID로 테스트 ===", count);
        
        // 테스트할 ID 목록 생성 (1부터 count까지)
        List<Long> ids = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            ids.add(i);
        }
        
        long startTime = System.currentTimeMillis();
        
        List<Author> authors = authorRepository.findByIdIn(ids);
        
        long queryTime = System.currentTimeMillis() - startTime;
        log.info("IN 쿼리 실행 시간: {}ms", queryTime);
        log.info("조회된 작가 수: {}", authors.size());
        
        // N+1 문제 발생 확인을 위해 books 접근
        long accessStartTime = System.currentTimeMillis();
        int totalBooks = 0;
        for (Author author : authors) {
            totalBooks += author.getBooks().size();
        }
        long accessTime = System.currentTimeMillis() - accessStartTime;
        
        log.info("연관관계 데이터 접근 시간: {}ms", accessTime);
        log.info("총 책 개수: {}", totalBooks);
        log.info("전체 실행 시간: {}ms", queryTime + accessTime);
        log.info("=== IN 쿼리 성능 테스트 완료 ===");
        
        return authors;
    }

    @Transactional(readOnly = true)
    public List<Author> testInQueryWithFetchJoin(int count) {
        log.info("=== IN 쿼리 + FETCH JOIN 성능 테스트 시작: {} 개의 ID로 테스트 ===", count);
        
        // 테스트할 ID 목록 생성 (1부터 count까지)
        List<Long> ids = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            ids.add(i);
        }
        
        long startTime = System.currentTimeMillis();
        
        List<Author> authors = authorRepository.findByIdInWithBooks(ids);
        
        long queryTime = System.currentTimeMillis() - startTime;
        log.info("IN + FETCH JOIN 쿼리 실행 시간: {}ms", queryTime);
        log.info("조회된 작가 수: {}", authors.size());
        
        // 이미 fetch join으로 로드되어 있으므로 추가 쿼리 없음
        long accessStartTime = System.currentTimeMillis();
        int totalBooks = 0;
        for (Author author : authors) {
            totalBooks += author.getBooks().size();
        }
        long accessTime = System.currentTimeMillis() - accessStartTime;
        
        log.info("연관관계 데이터 접근 시간: {}ms", accessTime);
        log.info("총 책 개수: {}", totalBooks);
        log.info("전체 실행 시간: {}ms", queryTime + accessTime);
        log.info("=== IN 쿼리 + FETCH JOIN 성능 테스트 완료 ===");
        
        return authors;
    }

    @Transactional(readOnly = true)
    public List<Author> testInQueryWithBatchSize(int count) {
        log.info("=== IN 쿼리 + @BatchSize 성능 테스트 시작: {} 개의 ID로 테스트 ===", count);
        
        // 테스트할 ID 목록 생성 (1부터 count까지)
        List<Long> ids = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            ids.add(i);
        }
        
        long startTime = System.currentTimeMillis();
        
        // @BatchSize가 적용된 일반 IN 쿼리
        List<Author> authors = authorRepository.findByIdIn(ids);
        
        long queryTime = System.currentTimeMillis() - startTime;
        log.info("IN 쿼리 실행 시간: {}ms", queryTime);
        log.info("조회된 작가 수: {}", authors.size());
        
        // @BatchSize로 인해 배치 쿼리 실행됨
        long accessStartTime = System.currentTimeMillis();
        int totalBooks = 0;
        for (Author author : authors) {
            totalBooks += author.getBooks().size();
        }
        long accessTime = System.currentTimeMillis() - accessStartTime;
        
        log.info("연관관계 데이터 접근 시간 (BatchSize 적용): {}ms", accessTime);
        log.info("총 책 개수: {}", totalBooks);
        log.info("전체 실행 시간: {}ms", queryTime + accessTime);
        log.info("=== IN 쿼리 + @BatchSize 성능 테스트 완료 ===");
        
        return authors;
    }

    @Transactional(readOnly = true)
    public List<Author> testInQueryWithBatchProcessing(int count) {
        log.info("=== IN 쿼리 배치 처리 성능 테스트 시작: {} 개의 ID로 테스트 ===", count);
        
        // 테스트할 ID 목록 생성 (1부터 count까지)
        List<Long> ids = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            ids.add(i);
        }
        
        long startTime = System.currentTimeMillis();
        
        List<Author> allAuthors = new ArrayList<>();
        int batchSize = 100; // 100개씩 배치 처리
        
        // ID를 100개씩 나누어 FETCH JOIN으로 처리
        for (int i = 0; i < ids.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, ids.size());
            List<Long> batch = ids.subList(i, endIndex);
            
            List<Author> batchResult = authorRepository.findByIdInWithBooks(batch);
            allAuthors.addAll(batchResult);
            
            log.info("배치 {}/{} 완료 ({}개)", (i/batchSize + 1), (ids.size() + batchSize - 1)/batchSize, batch.size());
        }
        
        long queryTime = System.currentTimeMillis() - startTime;
        log.info("배치 처리 전체 실행 시간: {}ms", queryTime);
        log.info("조회된 작가 수: {}", allAuthors.size());
        
        // 이미 FETCH JOIN으로 로드되어 있음
        long accessStartTime = System.currentTimeMillis();
        int totalBooks = 0;
        for (Author author : allAuthors) {
            totalBooks += author.getBooks().size();
        }
        long accessTime = System.currentTimeMillis() - accessStartTime;
        
        log.info("연관관계 데이터 접근 시간: {}ms", accessTime);
        log.info("총 책 개수: {}", totalBooks);
        log.info("전체 실행 시간: {}ms", queryTime + accessTime);
        log.info("=== IN 쿼리 배치 처리 성능 테스트 완료 ===");
        
        return allAuthors;
    }
}