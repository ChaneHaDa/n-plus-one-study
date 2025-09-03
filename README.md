# N+1 문제 & IN 쿼리 성능 최적화 스터디 🚀

JPA/Hibernate에서 발생하는 **N+1 문제**와 **IN 쿼리의 성능 저하** 문제를 실제 코드와 함께 학습하고 해결 방법을 비교해보는 프로젝트입니다.

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [N+1 문제란?](#n1-문제란)
- [IN 쿼리 성능 문제](#in-쿼리-성능-문제)
- [실행 방법](#실행-방법)
- [API 엔드포인트](#api-엔드포인트)
- [성능 테스트 시나리오](#성능-테스트-시나리오)
- [해결 방법 비교](#해결-방법-비교)

## 🎯 프로젝트 개요

이 프로젝트는 다음과 같은 내용을 다룹니다:

- **N+1 문제의 발생 원인과 해결 방법**
- **IN 쿼리에서 발생하는 성능 저하와 최적화 방법**
- **실제 SQL 로그를 통한 성능 비교**
- **다양한 JPA 최적화 기법 비교**

### 🏗️ 프로젝트 구조

```
Author (작가)  1 : N  Book (책)
- 1명의 작가는 여러 권의 책을 가질 수 있음
- Lazy Loading으로 설정되어 N+1 문제 발생 가능
- 500명의 작가, 약 1,500권의 책으로 테스트 데이터 구성
```

## 🔥 N+1 문제란?

### 문제 상황
```java
// 1번: 모든 작가 조회
List<Author> authors = authorRepository.findAll();

// N번: 각 작가의 책 조회 (Lazy Loading으로 인해 개별 쿼리 실행)
for (Author author : authors) {
    System.out.println(author.getBooks().size()); // 각각 개별 쿼리!
}
```

**결과**: 작가 10명을 조회할 때 **총 11번의 쿼리** 실행 (1 + 10)

### 발생 원인
- **Lazy Loading**: 연관관계 데이터를 실제 접근 시점에 조회
- **개별 쿼리**: 각 엔티티마다 별도의 SELECT 쿼리 실행
- **성능 저하**: 쿼리 수가 데이터 양에 비례해서 증가

## ⚡ IN 쿼리 성능 문제

### 일반적인 N+1 문제
```sql
-- Author 300명 조회
SELECT * FROM authors WHERE id IN (1,2,3,...,300);

-- 각 Author마다 Book 조회 (300번 실행!)
SELECT * FROM books WHERE author_id = 1;
SELECT * FROM books WHERE author_id = 2;
...
SELECT * FROM books WHERE author_id = 300;
```
**총 301번의 쿼리** 실행

### JOIN FETCH의 함정 ⚠️
```sql
-- 카테시안 곱 발생!
SELECT a.*, b.*
FROM authors a 
JOIN books b ON a.id = b.author_id 
WHERE a.id IN (1,2,3,...,300);
```

**문제점**:
- Author 300명 × 평균 Book 3권 = **900개 결과 행**
- 중복된 Author 데이터가 Book 개수만큼 반복 전송
- 메모리 사용량과 네트워크 트래픽 급증

### 성능 저하 구간
- **50개 이하**: 큰 문제 없음
- **100-300개**: 성능 저하 시작
- **300개 이상**: 심각한 성능 저하
- **1000개 이상**: 극심한 성능 저하 + DB 제한 가능성

## 🚀 실행 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. Swagger UI 접속
```
http://localhost:8080/docs
```

### 3. H2 Console 접속 (선택사항)
```
http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (없음)
```

### 4. 애플리케이션 시작 시
- **자동으로 500명의 작가와 약 1,500권의 책 데이터 생성**
- 콘솔 로그에서 데이터 생성 과정 확인 가능

## 📡 API 엔드포인트

### 🔍 N+1 문제 기본 테스트

| 메서드 | 엔드포인트 | 설명 | 특징 |
|--------|-----------|------|------|
| GET | `/api/authors/with-n-plus-one` | N+1 문제 발생 | 1 + N개 쿼리 실행 |
| GET | `/api/authors/without-n-plus-one` | Fetch Join으로 해결 | 1개 쿼리로 해결 |
| GET | `/api/authors/with-entity-graph` | EntityGraph로 해결 | 1개 쿼리로 해결 |
| GET | `/api/authors/with-jpql` | JPQL LEFT JOIN FETCH | 1개 쿼리로 해결 |

### ⚡ IN 쿼리 성능 테스트

| 메서드 | 엔드포인트 | 설명 | 성능 |
|--------|-----------|------|------|
| GET | `/api/authors/test-in-query/{count}` | N+1 문제 발생 | ❌ 최악 (1+N개 쿼리) |
| GET | `/api/authors/test-in-query-with-fetch/{count}` | FETCH JOIN | ⚠️ 카테시안 곱 문제 |
| GET | `/api/authors/test-in-query-with-batch-size/{count}` | @BatchSize 최적화 | ✅ 배치 쿼리 |
| GET | `/api/authors/test-in-query-batch-processing/{count}` | 배치 처리 최적화 | ✅ 최적 성능 |

## 🧪 성능 테스트 시나리오

### 1. 기본 N+1 문제 확인
```bash
# N+1 문제 발생 확인
GET /api/authors/with-n-plus-one

# 해결 방법들 성능 비교
GET /api/authors/without-n-plus-one
GET /api/authors/with-entity-graph  
GET /api/authors/with-jpql
```

### 2. IN 쿼리 성능 비교 (50개)
```bash
GET /api/authors/test-in-query/50              # N+1 문제
GET /api/authors/test-in-query-with-fetch/50   # FETCH JOIN
GET /api/authors/test-in-query-with-batch-size/50     # @BatchSize
GET /api/authors/test-in-query-batch-processing/50    # 배치 처리
```

### 3. 대량 데이터 성능 테스트 (300개)
```bash
GET /api/authors/test-in-query/300              # 301개 쿼리!
GET /api/authors/test-in-query-with-fetch/300   # 카테시안 곱 문제
GET /api/authors/test-in-query-with-batch-size/300     # ~3개 배치 쿼리
GET /api/authors/test-in-query-batch-processing/300    # 3개 최적화된 쿼리
```

### 4. 극한 테스트 (500개)
```bash
GET /api/authors/test-in-query/500              # 501개 쿼리!!!
GET /api/authors/test-in-query-batch-processing/500    # 5개 최적화된 쿼리
```

## 📊 해결 방법 비교

### 1️⃣ N+1 문제 해결 방법

| 방법 | 코드 | 장점 | 단점 | 권장도 |
|------|------|------|------|-------|
| **Fetch Join** | `@Query("SELECT DISTINCT a FROM Author a JOIN FETCH a.books")` | ✅ 명확하고 직관적<br>✅ 1개 쿼리로 해결 | ❌ DISTINCT 필요<br>❌ 카테시안 곱 가능성 | ⭐⭐⭐⭐⭐ |
| **EntityGraph** | `@EntityGraph(attributePaths = {"books"})` | ✅ 어노테이션으로 간편<br>✅ 1개 쿼리로 해결 | ❌ 복잡한 조건 처리 어려움 | ⭐⭐⭐⭐ |
| **JPQL LEFT JOIN** | `@Query("SELECT a FROM Author a LEFT JOIN FETCH a.books")` | ✅ NULL 데이터도 포함<br>✅ 1개 쿼리로 해결 | ❌ 카테시안 곱 가능성 | ⭐⭐⭐⭐ |
| **EAGER Loading** | `fetch = FetchType.EAGER` | ✅ 설정 간단 | ❌ 항상 조회하여 성능 저하<br>❌ 불필요한 데이터까지 로드 | ⭐ |

### 2️⃣ IN 쿼리 최적화 방법

| 방법 | 쿼리 수 | 성능 | 사용 시나리오 | 권장도 |
|------|---------|------|-------------|-------|
| **일반 IN 쿼리** | 1 + N개 | ❌ 최악 | 소량 데이터 (< 50개) | ⭐ |
| **IN + FETCH JOIN** | 1개 | ⚠️ 카테시안 곱 | 중간 데이터 (50-100개) | ⭐⭐ |
| **@BatchSize** | ~N/100개 | ✅ 좋음 | 대량 데이터 (100-500개) | ⭐⭐⭐⭐ |
| **배치 처리** | N/100개 | ✅ 최적 | 대량 데이터 (500개 이상) | ⭐⭐⭐⭐⭐ |

### 3️⃣ 실제 성능 비교 예시

**300명의 작가 조회 시:**

| 방법 | 쿼리 수 | 실행 시간 | 메모리 사용량 | 네트워크 트래픽 |
|------|---------|-----------|-------------|--------------|
| N+1 문제 | 301개 | ~500ms | 낮음 | 높음 |
| FETCH JOIN | 1개 | ~100ms | 높음 | 매우 높음 |
| @BatchSize | ~3개 | ~150ms | 보통 | 보통 |
| 배치 처리 | 3개 | ~120ms | 보통 | 낮음 |

## 🔧 핵심 기술 스택

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Hibernate**
- **H2 Database** (인메모리)
- **Swagger/OpenAPI 3**
- **Lombok**

## 📝 학습 포인트

### ✅ 꼭 기억해야 할 것들

1. **N+1 문제는 Lazy Loading + 반복문에서 발생**
2. **Fetch Join은 카테시안 곱을 주의해야 함**
3. **IN 쿼리는 300개 이상에서 성능 저하 심각**
4. **@BatchSize는 간단하고 효과적인 최적화**
5. **대량 데이터는 배치 처리가 가장 안전**

### 📊 성능 측정 방법

1. **SQL 로그 확인**: 실제 실행되는 쿼리 수 체크
2. **실행 시간 측정**: 각 방법별 응답 시간 비교  
3. **메모리 사용량**: JVM 힙 메모리 모니터링
4. **네트워크 트래픽**: DB-Application 간 데이터 전송량

## 🎯 결론

- **소량 데이터 (< 100개)**: Fetch Join이나 EntityGraph 사용
- **중간 데이터 (100-300개)**: @BatchSize 적용
- **대량 데이터 (300개 이상)**: 배치 처리 방식 권장
- **항상 SQL 로그를 확인하여 실제 성능 검증 필수!**

---

## 🤝 기여하기

이 프로젝트는 학습 목적으로 만들어졌습니다. 개선사항이나 새로운 테스트 케이스가 있으시면 언제든 기여해주세요!

## 📄 라이센스

이 프로젝트는 학습 목적으로 자유롭게 사용하실 수 있습니다.