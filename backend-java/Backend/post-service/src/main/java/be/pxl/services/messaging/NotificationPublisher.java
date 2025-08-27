package be.pxl.services.messaging;

import be.pxl.services.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

// be.pxl.services.messaging.NotificationPublisher (post-service)
@Component
public class NotificationPublisher {
    private final RabbitTemplate rabbit;
    public NotificationPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    public void publish(PostModerationEvent event) {
        rabbit.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);
    }
}

