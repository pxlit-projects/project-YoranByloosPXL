package be.pxl.services.messaging;

import be.pxl.services.services.NotificationService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class NotificationListenerTest {

    @Test
    void handle_approved_buildsApprovedMessage() {
        NotificationService service = mock(NotificationService.class);
        NotificationListener listener = new NotificationListener(service);

        PostModerationEvent evt =
                new PostModerationEvent(42L, "ann", "GOEDGEKEURD", null);

        listener.handle(evt);

        verify(service).createForUser("ann", "Je post #42 is goedgekeurd.");
    }

    @Test
    void handle_rejected_withReason_includesReason() {
        NotificationService service = mock(NotificationService.class);
        NotificationListener listener = new NotificationListener(service);

        PostModerationEvent evt =
                new PostModerationEvent(7L, "bob", "GEWEIGERD", "te kort");

        listener.handle(evt);

        verify(service).createForUser("bob", "Je post #7 is afgekeurd: te kort");
    }

    @Test
    void handle_rejected_withoutReason_noTrailingColon() {
        NotificationService service = mock(NotificationService.class);
        NotificationListener listener = new NotificationListener(service);

        PostModerationEvent evt =
                new PostModerationEvent(8L, "zoe", "GEWEIGERD", "  ");

        listener.handle(evt);

        verify(service).createForUser("zoe", "Je post #8 is afgekeurd");
    }
}
