package be.pxl.services.repository;

import be.pxl.services.controller.ReviewController;
import be.pxl.services.domain.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = be.pxl.services.TestBootApp.class)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findByPostId_returnsOnlyMatching() {
        Review r1 = new Review();
        r1.setPostId(1L);
        r1.setReviewer("alice");
        r1.setComment("ok");
        r1.setCreatedAt(LocalDateTime.now());

        Review r2 = new Review();
        r2.setPostId(2L);
        r2.setReviewer("bob");
        r2.setComment("meh");
        r2.setCreatedAt(LocalDateTime.now());

        reviewRepository.saveAll(List.of(r1, r2));

        List<Review> only1 = reviewRepository.findByPostId(1L);

        assertThat(only1).hasSize(1);
        assertThat(only1.get(0).getReviewer()).isEqualTo("alice");
    }
}
