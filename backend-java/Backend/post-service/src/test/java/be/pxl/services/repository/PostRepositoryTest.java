package be.pxl.services.repository;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository repo;

    private LocalDateTime day1 = LocalDateTime.of(2025, 9, 25, 10, 0);
    private LocalDateTime day2 = LocalDateTime.of(2025, 9, 26, 10, 0);

    @BeforeEach
    void seed() {
        repo.save(Post.builder()
                .title("Post 1")
                .content("Dit is een post")
                .author("yoran")
                .createdAt(day1)
                .updatedAt(day1)
                .published(true)
                .status(PostStatus.GEPUBLICEERD)
                .build());

        // published andere auteur
        repo.save(Post.builder()
                .title("Ander artikel")
                .content("random content")
                .author("mieke")
                .createdAt(day1)
                .updatedAt(day1)
                .published(true)
                .status(PostStatus.GEPUBLICEERD)
                .build());

        // not published => nooit terugkomen
        repo.save(Post.builder()
                .title("Verborgen")
                .content("hidden")
                .author("yoran")
                .createdAt(day1)
                .updatedAt(day1)
                .published(false)
                .status(PostStatus.CONCEPT)
                .build());

        // published maar andere dag
        repo.save(Post.builder()
                .title("Post dag 2")
                .content("iets")
                .author("yoran")
                .createdAt(day2)
                .updatedAt(day2)
                .published(true)
                .status(PostStatus.GEPUBLICEERD)
                .build());
    }

    @Test
    void filterByKeyword_onlyPublished_partialMatch() {
        var res = repo.filterPublished("post", null, null, null);
        // "Post 1" en "Post dag 2" bevatten 'post' (case-insensitive)
        assertThat(res).extracting(Post::getTitle)
                .contains("Post 1", "Post dag 2")
                .doesNotContain("Verborgen", "Ander artikel");
    }

    @Test
    void filterByAuthor_partial() {
        var res = repo.filterPublished(null, "yor", null, null);
        assertThat(res).hasSize(2); // yoran dag1 + dag2
        assertThat(res).allMatch(p -> p.getAuthor().equals("yoran"));
    }

    @Test
    void filterByDate_dayRange() {
        var start = LocalDateTime.of(2025, 9, 25, 0, 0);
        var end   = start.plusDays(1);
        var res = repo.filterPublished(null, null, start, end);
        // enkel published van die dag (2e dag valt buiten range)
        assertThat(res).extracting(Post::getCreatedAt).allMatch(dt ->
                !dt.isBefore(start) && dt.isBefore(end));
        assertThat(res).hasSize(2); // Post 1 + Ander artikel (beide published op day1)
    }

    @Test
    void filterByAll_threeCriteria() {
        var start = LocalDateTime.of(2025, 9, 25, 0, 0);
        var end   = start.plusDays(1);
        var res = repo.filterPublished("post", "yor", start, end);
        // dag1 + keyword + auteur = enkel "Post 1"
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getTitle()).isEqualTo("Post 1");
    }
}
