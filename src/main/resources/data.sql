-- 작가 데이터
INSERT INTO authors (name, email) VALUES ('김영하', 'younha.kim@example.com');
INSERT INTO authors (name, email) VALUES ('한강', 'han.kang@example.com');
INSERT INTO authors (name, email) VALUES ('조정래', 'jungrae.cho@example.com');
INSERT INTO authors (name, email) VALUES ('박민규', 'mingyu.park@example.com');
INSERT INTO authors (name, email) VALUES ('이승우', 'seungwoo.lee@example.com');

-- 책 데이터
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('검은꽃', '978-8932917245', '2007-05-15', 1);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('빛의 제국', '978-8954618456', '2006-03-10', 1);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('살인자의 기억법', '978-8954646567', '2013-07-20', 1);

INSERT INTO books (title, isbn, published_date, author_id) VALUES ('채식주의자', '978-8936434120', '2007-10-30', 2);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('흰', '978-8936434458', '2016-11-28', 2);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('작별하지 않는다', '978-8936438566', '2021-04-20', 2);

INSERT INTO books (title, isbn, published_date, author_id) VALUES ('태백산맥', '978-8965700234', '1989-08-15', 3);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('아리랑', '978-8965700567', '1994-03-01', 3);

INSERT INTO books (title, isbn, published_date, author_id) VALUES ('지구영웅전설', '978-8956602345', '2003-12-10', 4);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('카스테라', '978-8956603456', '2005-09-22', 4);

INSERT INTO books (title, isbn, published_date, author_id) VALUES ('생의 이면', '978-8934962123', '2002-06-18', 5);
INSERT INTO books (title, isbn, published_date, author_id) VALUES ('나무 위의 군대', '978-8934962234', '2009-11-05', 5);