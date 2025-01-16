package be.pxl.services.services;
import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final PostClient postClient;

    public ReviewService(ReviewRepository reviewRepository, PostClient postClient) {
        this.reviewRepository = reviewRepository;
        this.postClient = postClient;
    }

    @Override
    public List<Review> getReviewsByPostId(Long postId) {
        return reviewRepository.findByPostId(postId);
    }

    @Override
    public Review rejectPostWithReview(Long postId, String reviewer, String comment) {
        Review review = new Review();
        review.setPostId(postId);
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        postClient.approvePost(postId);
        return review;
    }

    @Override
    public void approvePost(Long postId) {
        postClient.approvePost(postId);
    }

}
