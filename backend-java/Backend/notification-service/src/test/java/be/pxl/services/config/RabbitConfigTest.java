package be.pxl.services.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitConfigTest {

    private final RabbitConfig config = new RabbitConfig();

    @Test
    void exchangeIsConfigured() {
        TopicExchange ex = config.moderationExchange();
        assertThat(ex.getName()).isEqualTo(RabbitConfig.EXCHANGE);
        assertThat(ex.isDurable()).isTrue();
        assertThat(ex.isAutoDelete()).isFalse();
    }

    @Test
    void messageConverterIsJackson() {
        MessageConverter mc = config.jacksonConverter();
        assertThat(mc).isInstanceOf(Jackson2JsonMessageConverter.class);
    }

    @Test
    void rabbitTemplateUsesConnectionFactoryAndConverter() {
        ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);
        MessageConverter mc = config.jacksonConverter();

        RabbitTemplate rt = config.rabbitTemplate(cf, mc);

        assertThat(rt.getConnectionFactory()).isSameAs(cf);
        assertThat(rt.getMessageConverter()).isSameAs(mc);
    }
}
