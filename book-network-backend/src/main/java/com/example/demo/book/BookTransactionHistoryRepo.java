package com.example.demo.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookTransactionHistoryRepo extends JpaRepository<BookTransactionHistory,Integer> {
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :id
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer id);
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :id
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer id);
    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.user.id = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId,@Param("userId") Integer userId);
    @Query("""
            SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.createdBy != :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = false
            AND transaction.returnApproved = false
            """

    )
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);
    @Query("""
            SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.createdBy = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = true
            AND transaction.returnApproved = false
            """

    )
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);
}
