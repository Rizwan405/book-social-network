package com.example.demo.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Integer>, JpaSpecificationExecutor<Book> {
    @Query("""
            SELECT book
            FROM Book book
            
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);
    List<Book> findAll();
}
