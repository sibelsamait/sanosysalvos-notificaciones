package com.sanosysalvos.notificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Coincidencias (matching-service) ---
    public static final String MATCHING_EXCHANGE = "matching-exchange";
    public static final String MATCHING_COINCIDENCIA_QUEUE = "coincidencia-potencial-queue";
    public static final String MATCHING_ROUTING_KEY = "coincidencia.potencial";

    // --- Primer mensaje de chat (chat-service) ---
    public static final String CHAT_EXCHANGE = "chat-exchange";
    public static final String CHAT_PRIMER_MENSAJE_QUEUE = "chat-primer-mensaje-queue";
    public static final String CHAT_ROUTING_KEY = "chat.primer-mensaje";

    @Bean
    public TopicExchange matchingExchange() {
        return new TopicExchange(MATCHING_EXCHANGE);
    }

    @Bean
    public Queue matchingCoincidenciaQueue() {
        return new Queue(MATCHING_COINCIDENCIA_QUEUE, true);
    }

    @Bean
    public Binding matchingBinding() {
        return BindingBuilder.bind(matchingCoincidenciaQueue())
                .to(matchingExchange())
                .with(MATCHING_ROUTING_KEY);
    }

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE);
    }

    @Bean
    public Queue chatPrimerMensajeQueue() {
        return new Queue(CHAT_PRIMER_MENSAJE_QUEUE, true);
    }

    @Bean
    public Binding chatBinding() {
        return BindingBuilder.bind(chatPrimerMensajeQueue())
                .to(chatExchange())
                .with(CHAT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}