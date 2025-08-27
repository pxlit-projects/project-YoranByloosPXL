package be.pxl.services.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "post.moderation";
    public static final String QUEUE = "post.moderation.notifications";
    public static final String ROUTING_KEY = "post.moderation";

    @Bean TopicExchange moderationExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Queue notificationsQueue() { return QueueBuilder.durable(QUEUE).build(); }
    @Bean Binding bind() { return BindingBuilder.bind(notificationsQueue()).to(moderationExchange()).with(ROUTING_KEY); }

    @Bean MessageConverter jacksonConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }
}
