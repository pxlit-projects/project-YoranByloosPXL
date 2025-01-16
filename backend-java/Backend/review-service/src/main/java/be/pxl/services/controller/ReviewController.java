package be.pxl.services.controller;

import be.pxl.services.domain.Review;
import be.pxl.services.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Review>> getReviewsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(reviewService.getReviewsByPostId(postId));
    }

    @PostMapping("/{postId}/reject")
    public ResponseEntity<Review> rejectPostWithReview(@PathVariable Long postId,
                                                       @RequestHeader("username") String reviewer,
                                                       @RequestParam String comment) {
        Review review = reviewService.rejectPostWithReview(postId, reviewer, comment);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/{postId}/approve")
    public ResponseEntity<Void> approvePost(@PathVariable Long postId) {
        reviewService.approvePost(postId);
        return ResponseEntity.ok().build();
    }
}