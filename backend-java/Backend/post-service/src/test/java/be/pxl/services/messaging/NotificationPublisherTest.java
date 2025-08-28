package be.pxl.services.messaging;

import be.pxl.services.config.RabbitConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    @Mock
    RabbitTemplate rabbit;

    @InjectMocks
    NotificationPublisher publisher;

    @Test
    void publish_verstuurt_event_naar_exchange_met_routingKey() {
        PostModerationEvent event = mock(PostModerationEvent.class);

        // Act
        publisher.publish(event);

        // Assert
        verify(rabbit, times(1))
                .convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);
        verifyNoMoreInteractions(rabbit);
    }
}
