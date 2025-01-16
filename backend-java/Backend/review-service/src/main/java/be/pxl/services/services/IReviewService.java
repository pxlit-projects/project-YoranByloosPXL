package be.pxl.services.services;

import be.pxl.services.domain.Review;

import java.util.List;

public interface IReviewService {
    List<Review> getReviewsByPostId(Long postId);
    void approvePost(Long postId);
    Review rejectPostWithReview(Long postId, String reviewer, String comment);
}
