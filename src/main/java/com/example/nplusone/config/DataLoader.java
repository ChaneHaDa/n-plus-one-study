package com.example.nplusone.config;

import com.example.nplusone.entity.Author;
import com.example.nplusone.entity.Book;
import com.example.nplusone.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final AuthorRepository authorRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (authorRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다. 초기 데이터 생성을 건너뜁니다.");
            return;
        }
        
        log.info("=== 대량 테스트 데이터 생성 시작 ===");
        
        List<Author> authors = new ArrayList<>();
        
        // 500명의 작가 생성
        for (int i = 1; i <= 500; i++) {
            Author author = new Author("작가" + i, "author" + i + "@example.com");
            
            // 각 작가마다 1-5권의 책 생성
            int bookCount = (i % 5) + 1;
            for (int j = 1; j <= bookCount; j++) {
                Book book = new Book(
                    "작가" + i + "의 책 " + j,
                    "ISBN-" + i + "-" + j,
                    LocalDate.now().minusDays(i * j),
                    author
                );
                author.getBooks().add(book);
            }
            
            authors.add(author);
            
            // 배치 처리 (100개씩)
            if (i % 100 == 0) {
                authorRepository.saveAll(authors);
                log.info("{}명의 작가 데이터 저장 완료", i);
                authors.clear();
            }
        }
        
        // 나머지 데이터 저장
        if (!authors.isEmpty()) {
            authorRepository.saveAll(authors);
        }
        
        log.info("=== 대량 테스트 데이터 생성 완료: 총 {}명의 작가, {}권의 책 ===", 
                authorRepository.count(), 
                authorRepository.findAll().stream().mapToInt(a -> a.getBooks().size()).sum());
    }
}