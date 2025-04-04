package com.example.demo.feeback;

import com.example.demo.book.Book;
import com.example.demo.book.BookRepository;
import com.example.demo.common.PageResponse;
import com.example.demo.exception.OperationNotPermittedException;
import com.example.demo.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final  BookRepository bookRepo;
    private final  FeedBackMapper mapper;
    private final  FeedBackRepository feedBackRepository;
    public Integer saveFeedBack(FeedBackRequest feedbackRequest, Authentication connectedUser) {
//        need an actual response object ,
//        save it
//        fetch the book
        Book book = bookRepo.findById(feedbackRequest.bookId()).orElseThrow(()->new EntityNotFoundException("Book with the Id::"+ feedbackRequest.bookId() + "not found"));
        User user = ((User) connectedUser.getPrincipal());
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You did'nt give feedback to archived / not shareable book");
        }
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You did'nt have permission to give feeback to your own book");

        }
        Feedback feedback = mapper.toFeedBack(feedbackRequest);
        feedBackRepository.save(feedback);
        return feedBackRepository.save(feedback).getId();
    }

    @Transactional
    public PageResponse<FeedBackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedBackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> mapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }
}
