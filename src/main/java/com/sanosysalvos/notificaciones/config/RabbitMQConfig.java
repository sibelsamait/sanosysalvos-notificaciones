package com.sanosysalvos.notificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Define la topología de mensajería del servicio de notificaciones:
 *
 * Exchange (topic):  sanosysalvos.notificaciones.exchange
 *
 * Routing keys que otros microservicios deben usar al publicar:
 *  - chat.mensaje.primero            -> publicado por sanosysalvos-chat-services
 *                                        SOLO en el primer mensaje de una conversación.
 *  - matching.coincidencia.detectada -> publicado por sanosysalvos-matching-service
 *                                        por cada coincidencia calculada (el filtro
 *                                        de "> 85%" se aplica aquí, en el listener).
 *
 * Cada cola tiene su propia Dead Letter Queue: si el envío de correo falla
 * de forma definitiva (después de los reintentos), el mensaje termina en la DLQ
 * en lugar de perderse o reintentarse infinitamente.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "sanosysalvos.notificaciones.exchange";
    public static final String DLX = "sanosysalvos.notificaciones.dlx";

    public static final String CHAT_PRIMER_MENSAJE_QUEUE = "notificaciones.chat.primer-mensaje.queue";
    public static final String CHAT_PRIMER_MENSAJE_ROUTING_KEY = "chat.mensaje.primero";
    public static final String CHAT_PRIMER_MENSAJE_DLQ = "notificaciones.chat.primer-mensaje.dlq";

    public static final String MATCHING_COINCIDENCIA_QUEUE = "notificaciones.matching.coincidencia.queue";
    public static final String MATCHING_COINCIDENCIA_ROUTING_KEY = "matching.coincidencia.detectada";
    public static final String MATCHING_COINCIDENCIA_DLQ = "notificaciones.matching.coincidencia.dlq";

    // ---------- Exchanges ----------

    @Bean
    public TopicExchange notificacionesExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    // ---------- Cola: primer mensaje de chat ----------

    @Bean
    public Queue chatPrimerMensajeQueue() {
        return QueueBuilder.durable(CHAT_PRIMER_MENSAJE_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", CHAT_PRIMER_MENSAJE_DLQ)
                .build();
    }

    @Bean
    public Queue chatPrimerMensajeDlq() {
        return QueueBuilder.durable(CHAT_PRIMER_MENSAJE_DLQ).build();
    }

    @Bean
    public Binding chatPrimerMensajeBinding() {
        return BindingBuilder.bind(chatPrimerMensajeQueue())
                .to(notificacionesExchange())
                .with(CHAT_PRIMER_MENSAJE_ROUTING_KEY);
    }

    @Bean
    public Binding chatPrimerMensajeDlqBinding() {
        return BindingBuilder.bind(chatPrimerMensajeDlq())
                .to(deadLetterExchange())
                .with(CHAT_PRIMER_MENSAJE_DLQ);
    }

    // ---------- Cola: coincidencia de matching ----------

    @Bean
    public Queue matchingCoincidenciaQueue() {
        return QueueBuilder.durable(MATCHING_COINCIDENCIA_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", MATCHING_COINCIDENCIA_DLQ)
                .build();
    }

    @Bean
    public Queue matchingCoincidenciaDlq() {
        return QueueBuilder.durable(MATCHING_COINCIDENCIA_DLQ).build();
    }

    @Bean
    public Binding matchingCoincidenciaBinding() {
        return BindingBuilder.bind(matchingCoincidenciaQueue())
                .to(notificacionesExchange())
                .with(MATCHING_COINCIDENCIA_ROUTING_KEY);
    }

    @Bean
    public Binding matchingCoincidenciaDlqBinding() {
        return BindingBuilder.bind(matchingCoincidenciaDlq())
                .to(deadLetterExchange())
                .with(MATCHING_COINCIDENCIA_DLQ);
    }

    // ---------- Serialización JSON <-> DTO ----------

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
