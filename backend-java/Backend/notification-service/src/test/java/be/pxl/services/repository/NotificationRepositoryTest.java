package be.pxl.services.repository;

import be.pxl.services.domain.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = be.pxl.services.TestBootApp.class)
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository repo;

    @Test
    void findByRecipientOrderByCreatedAtDesc_returnsNewestFirst() {
        Notification n1 = new Notification("ann", "older");
        n1.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        Notification n2 = new Notification("ann", "newer");
        n2.setCreatedAt(LocalDateTime.now());

        repo.saveAll(List.of(n1, n2));

        List<Notification> result = repo.findByRecipientOrderByCreatedAtDesc("ann");

        assertThat(result).extracting(Notification::getMessage)
                .containsExactly("newer", "older");
    }

    @Test
    void deleteByIdAndRecipient_deletesAndReturnsCount() {
        Notification n = repo.save(new Notification("bob", "to delete"));
        long count = repo.deleteByIdAndRecipient(n.getId(), "bob");
        assertThat(count).isEqualTo(1);
        assertThat(repo.findAll()).isEmpty();
    }

    @Test
    void deleteAllByRecipient_removesAllForUser() {
        repo.save(new Notification("carl", "1"));
        repo.save(new Notification("carl", "2"));
        repo.save(new Notification("dana", "keep"));

        repo.deleteAllByRecipient("carl");

        assertThat(repo.findByRecipientOrderByCreatedAtDesc("carl")).isEmpty();
        assertThat(repo.findByRecipientOrderByCreatedAtDesc("dana")).hasSize(1);
    }
}
