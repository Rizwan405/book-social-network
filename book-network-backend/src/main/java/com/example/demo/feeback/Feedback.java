package com.example.demo.feeback;

import com.example.demo.book.Book;
import com.example.demo.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Feedback extends BaseEntity {

    private Double note; // use to measure the score 1-5
    private String comment;
//    defining relation between book and feedback
//    many feedback on many books
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;


}
