package be.pxl.services.services;

import be.pxl.services.domain.Notification;
import be.pxl.services.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private final NotificationRepository repo = mock(NotificationRepository.class);
    private final NotificationService service = new NotificationService(repo);

    @Test
    void createForUser_savesNotification() {
        when(repo.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification saved = service.createForUser("ann", "hi");

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        Notification n = cap.getValue();

        assertThat(saved.getRecipient()).isEqualTo("ann");
        assertThat(saved.getMessage()).isEqualTo("hi");
        assertThat(n.getRecipient()).isEqualTo("ann");
        assertThat(n.getMessage()).isEqualTo("hi");
    }

    @Test
    void getForUser_returnsSortedFromRepo() {
        when(repo.findByRecipientOrderByCreatedAtDesc("bob")).thenReturn(List.of(
                new Notification("bob", "B"), new Notification("bob", "A")
        ));

        List<Notification> list = service.getForUser("bob");

        assertThat(list).hasSize(2);
        verify(repo).findByRecipientOrderByCreatedAtDesc("bob");
    }

    @Test
    void deleteOneForUser_okWhenFound() {
        when(repo.deleteByIdAndRecipient(7L, "zoe")).thenReturn(1L);

        service.deleteOneForUser(7L, "zoe");

        verify(repo).deleteByIdAndRecipient(7L, "zoe");
    }

    @Test
    void deleteOneForUser_throws404WhenMissing() {
        when(repo.deleteByIdAndRecipient(99L, "zoe")).thenReturn(0L);

        assertThatThrownBy(() -> service.deleteOneForUser(99L, "zoe"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void deleteAllForUser_delegates() {
        service.deleteAllForUser("ann");
        verify(repo).deleteAllByRecipient("ann");
    }
}
