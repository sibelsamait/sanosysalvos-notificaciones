package com.sanosysalvos.notificaciones.listener;

import com.sanosysalvos.notificaciones.config.RabbitMQConfig;
import com.sanosysalvos.notificaciones.dto.ChatPrimerMensajeEventDTO;
import com.sanosysalvos.notificaciones.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Escucha la cola de "primer mensaje de chat" y dispara el correo de aviso.
 * sanosysalvos-chat-services debe publicar en esta cola SOLO la primera vez
 * que se crea un mensaje para un chatId dado (esa lógica vive en el chat-service,
 * no aquí).
 */
@Component
public class ChatEventListener {

    private static final Logger log = LoggerFactory.getLogger(ChatEventListener.class);

    private final EmailService emailService;

    public ChatEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.CHAT_PRIMER_MENSAJE_QUEUE)
    public void onPrimerMensaje(ChatPrimerMensajeEventDTO evento) {
        log.info("Evento recibido: primer mensaje de chat {}", evento.getChatId());
        emailService.enviarAvisoPrimerMensajeChat(evento);
    }
}
