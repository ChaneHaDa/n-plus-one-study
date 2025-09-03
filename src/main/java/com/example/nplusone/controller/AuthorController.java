package com.example.nplusone.controller;

import com.example.nplusone.entity.Author;
import com.example.nplusone.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Author API", description = "N+1 문제를 보여주는 작가 관련 API")
public class AuthorController {
    
    private final AuthorService authorService;

    @GetMapping("/with-n-plus-one")
    @Operation(summary = "N+1 문제가 발생하는 작가 조회", 
               description = "모든 작가와 책을 조회하되 N+1 문제가 발생합니다. 로그를 확인하여 쿼리 개수를 확인해보세요.")
    public List<Author> getAuthorsWithNPlusOneProblem() {
        return authorService.findAllAuthorsWithNPlusOneProblem();
    }

    @GetMapping("/without-n-plus-one")
    @Operation(summary = "N+1 문제를 해결한 작가 조회 (Fetch Join)", 
               description = "JOIN FETCH를 사용하여 N+1 문제를 해결한 작가 조회입니다. 로그를 확인하여 쿼리 개수를 비교해보세요.")
    public List<Author> getAuthorsWithoutNPlusOneProblem() {
        return authorService.findAllAuthorsWithoutNPlusOneProblem();
    }

    @GetMapping("/with-entity-graph")
    @Operation(summary = "N+1 문제를 해결한 작가 조회 (EntityGraph)", 
               description = "@EntityGraph를 사용하여 N+1 문제를 해결한 작가 조회입니다. 로그를 확인하여 쿼리 개수를 비교해보세요.")
    public List<Author> getAuthorsWithEntityGraph() {
        return authorService.findAllAuthorsWithEntityGraph();
    }

    @GetMapping("/with-jpql")
    @Operation(summary = "N+1 문제를 해결한 작가 조회 (JPQL LEFT JOIN FETCH)", 
               description = "JPQL LEFT JOIN FETCH를 사용하여 N+1 문제를 해결한 작가 조회입니다. 로그를 확인하여 쿼리 개수를 비교해보세요.")
    public List<Author> getAuthorsWithJpql() {
        return authorService.findAllAuthorsWithJpql();
    }
}