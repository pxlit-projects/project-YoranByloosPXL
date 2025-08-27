package be.pxl.services.repository;

import be.pxl.services.domain.Bookmark;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = be.pxl.services.TestBootApp.class)
class BookmarkRepositoryTest {
    @Autowired BookmarkRepository repository;

    @Test
    void existsByUsernameAndPostId_works() {
        Bookmark b = new Bookmark();
        b.setUsername("ann"); b.setPostId(10L); b.setCreatedAt(LocalDateTime.now());
        repository.save(b);

        boolean exists = repository.existsByUsernameAndPostId("ann", 10L);
        assertThat(exists).isTrue();
    }

    @Test
    void findByUsernameOrderByCreatedAtDesc_returnsInDescOrder() {
        Bookmark b1 = new Bookmark();
        b1.setUsername("ann"); b1.setPostId(1L); b1.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        Bookmark b2 = new Bookmark();
        b2.setUsername("ann"); b2.setPostId(2L); b2.setCreatedAt(LocalDateTime.now());

        repository.saveAll(List.of(b1, b2));

        List<Bookmark> list = repository.findByUsernameOrderByCreatedAtDesc("ann");
        assertThat(list).hasSize(2);
        assertThat(list.get(0).getPostId()).isEqualTo(2L);
        assertThat(list.get(1).getPostId()).isEqualTo(1L);
    }

    @Test
    void deleteByUsernameAndPostId_returnsCount() {
        Bookmark b = new Bookmark();
        b.setUsername("ann"); b.setPostId(33L); b.setCreatedAt(LocalDateTime.now());
        repository.save(b);

        long deleted = repository.deleteByUsernameAndPostId("ann", 33L);
        assertThat(deleted).isEqualTo(1L);
        assertThat(repository.existsByUsernameAndPostId("ann", 33L)).isFalse();
    }
}
