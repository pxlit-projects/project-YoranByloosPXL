package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService implements IReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final PostClient postClient;

    public ReviewService(ReviewRepository reviewRepository, PostClient postClient) {
        this.reviewRepository = reviewRepository;
        this.postClient = postClient;
    }

    @Override
    public List<Post> getReviewablePosts() {
        List<Post> posts = postClient.getReviewablePosts();
        log.info("Fetched reviewable posts count={}", posts.size());
        return posts;
    }

    @Override
    public List<Review> getReviewsByPostId(Long postId) {
        List<Review> reviews = reviewRepository.findByPostId(postId);
        log.info("Fetched reviews for postId={} count={}", postId, reviews.size());
        return reviews;
    }

    @Override
    public Review rejectPostWithReview(Long postId, String reviewer, String comment) {
        log.info("Reject post requested postId={} reviewer={}", postId, reviewer);
        Review review = new Review();
        review.setPostId(postId);
        review.setReviewer(reviewer);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        postClient.disapprovePost(postId);
        return saved;
    }

    @Override
    public void approvePost(Long postId) {
        log.info("Approve post requested postId={}", postId);
        postClient.approvePost(postId);
    }
}
