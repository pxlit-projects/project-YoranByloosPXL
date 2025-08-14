package be.pxl.services.controller;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviewable")
    public List<Post> getReviewablePosts() {
        log.info("GetReviewablePosts called");
        return reviewService.getReviewablePosts();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Review>> getReviewsByPostId(@PathVariable Long postId) {
        log.info("GetReviewsByPostId called postId={}", postId);
        return ResponseEntity.ok(reviewService.getReviewsByPostId(postId));
    }

    @PostMapping("/{postId}/reject")
    public ResponseEntity<Review> rejectPostWithReview(@PathVariable Long postId,
                                                       @RequestHeader("username") String reviewer,
                                                       @RequestParam String comment) {
        log.info("RejectPost requested postId={} by reviewer={}", postId, reviewer);
        Review review = reviewService.rejectPostWithReview(postId, reviewer, comment);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/{postId}/approve")
    public ResponseEntity<Void> approvePost(@PathVariable Long postId) {
        log.info("ApprovePost requested postId={}", postId);
        reviewService.approvePost(postId);
        return ResponseEntity.ok().build();
    }
}
