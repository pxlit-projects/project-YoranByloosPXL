package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    PostClient postClient;

    @InjectMocks
    ReviewService reviewService;

    @Test
    void getReviewablePosts_delegatesToPostClient() {
        Post p = new Post();
        p.setId(1L);
        when(postClient.getReviewablePosts("r")).thenReturn(List.of(p));

        List<Post> result = reviewService.getReviewablePosts("r");

        assertThat(result).hasSize(1);
        verify(postClient).getReviewablePosts("r");
    }

    @Test
    void getReviewsByPostId_readsFromRepo() {
        Review r = new Review();
        r.setId(10L);
        when(reviewRepository.findByPostId(5L)).thenReturn(List.of(r));

        List<Review> list = reviewService.getReviewsByPostId(5L);

        assertThat(list).extracting(Review::getId).containsExactly(10L);
        verify(reviewRepository).findByPostId(5L);
    }

    @Test
    void rejectPostWithReview_savesAndDisapproves() {
        when(reviewRepository.save(any())).thenAnswer(inv -> {
            Review toSave = inv.getArgument(0);
            toSave.setId(77L);
            return toSave;
        });

        Review saved = reviewService.rejectPostWithReview(6L, "rev", "bad");

        assertThat(saved.getId()).isEqualTo(77L);
        assertThat(saved.getPostId()).isEqualTo(6L);
        assertThat(saved.getReviewer()).isEqualTo("rev");
        assertThat(saved.getComment()).isEqualTo("bad");

        ArgumentCaptor<Review> cap = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(cap.capture());
        assertThat(cap.getValue().getCreatedAt()).isNotNull();

        verify(postClient).disapprovePost(6L);
    }

    @Test
    void approvePost_callsPostClient() {
        reviewService.approvePost(12L);
        verify(postClient).approvePost(12L);
    }
}
