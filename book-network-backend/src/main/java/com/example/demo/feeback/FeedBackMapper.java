package com.example.demo.feeback;

import com.example.demo.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedBackMapper {
//    to convert data carrier to object entity
    public Feedback toFeedBack(FeedBackRequest feedBackRequest){
        return Feedback.builder()
                        .note(feedBackRequest.note())
                        .comment(feedBackRequest.comment())
                .book(Book.builder().id(feedBackRequest.bookId()).archived(false).shareable(false).build())
                        .build();
    }
    public FeedBackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedBackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
