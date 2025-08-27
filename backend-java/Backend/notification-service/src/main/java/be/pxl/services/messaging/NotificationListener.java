package be.pxl.services.messaging;

import be.pxl.services.config.RabbitConfig;
import be.pxl.services.services.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private final NotificationService notifications;

    public NotificationListener(NotificationService notifications) { this.notifications = notifications; }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(PostModerationEvent event) {
        String msg = "GOEDGEKEURD".equalsIgnoreCase(event.getStatus())
                ? "Je post #" + event.getPostId() + " is goedgekeurd."
                : "Je post #" + event.getPostId() + " is afgekeurd" +
                (event.getReason() == null || event.getReason().isBlank() ? "" : (": " + event.getReason()));
        notifications.createForUser(event.getAuthorUsername(), msg);
    }
}
