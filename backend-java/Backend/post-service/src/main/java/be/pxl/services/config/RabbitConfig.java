// be.pxl.services.messaging.RabbitConfig
package be.pxl.services.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// be.pxl.services.config.RabbitConfig (post-service)
@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "post.moderation";
    public static final String ROUTING_KEY = "post.moderation";

    @Bean TopicExchange moderationExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean
    MessageConverter jacksonConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter conv) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }
}

