package com.example.demo.book;

import com.example.demo.common.BaseEntity;
import com.example.demo.feeback.Feedback;
import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;
//    relation between user and book
//    many user own 1 book in other many book owns 1 user
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
//    relation between feedback and book
//    1 book have multiple feedbacks
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;
//    defining relationship between book and book transaction
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;
    @Transient
    public double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate = feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0.0);
        double roundedRate = Math.round(rate * 10.0)/10.0;
        return roundedRate;
    }
}
