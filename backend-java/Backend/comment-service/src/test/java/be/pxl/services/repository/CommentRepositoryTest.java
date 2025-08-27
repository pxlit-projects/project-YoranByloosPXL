package be.pxl.services.repository;

import be.pxl.services.TestBootApp;
import be.pxl.services.domain.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = TestBootApp.class)
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Test
    void findByPostId_returnsOnlyMatching() {
        var now = LocalDateTime.now();

        var c1 = new Comment();
        c1.setPostId(5L);
        c1.setUsername("alice");
        c1.setContent("first!");
        c1.setCreatedAt(now);
        c1.setUpdatedAt(now);
        repository.save(c1);

        var c2 = new Comment();
        c2.setPostId(5L);
        c2.setUsername("bob");
        c2.setContent("nice post");
        c2.setCreatedAt(now);
        c2.setUpdatedAt(now);
        repository.save(c2);

        var c3 = new Comment();
        c3.setPostId(9L);
        c3.setUsername("charlie");
        c3.setContent("off-topic");
        c3.setCreatedAt(now);
        c3.setUpdatedAt(now);
        repository.save(c3);

        List<Comment> result = repository.findByPostId(5L);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Comment::getUsername).containsExactlyInAnyOrder("alice", "bob");
    }
}
