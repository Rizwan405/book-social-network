package com.example.demo.feeback;

import com.example.demo.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedbacks")
@Tag(name="Feedback")
@RequiredArgsConstructor
public class FeedBackController {
   private final  FeedBackService feedBackService;
    @PostMapping()
    public ResponseEntity<Integer> saveFeedBack(@Valid @RequestBody FeedBackRequest feedbackRequest, Authentication connectedUser){
//        it takes an Request object called FeedbackRequest
        return ResponseEntity.ok().body(feedBackService.saveFeedBack(feedbackRequest,connectedUser));
    }
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedBackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedBackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }
}
