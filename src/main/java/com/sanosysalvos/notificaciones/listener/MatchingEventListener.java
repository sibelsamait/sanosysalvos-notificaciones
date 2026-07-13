package com.sanosysalvos.notificaciones.listener;

import com.sanosysalvos.notificaciones.config.RabbitMQConfig;
import com.sanosysalvos.notificaciones.dto.MatchingCoincidenciaEventDTO;
import com.sanosysalvos.notificaciones.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MatchingEventListener {

    private static final Logger log = LoggerFactory.getLogger(MatchingEventListener.class);

    private final EmailService emailService;

    public MatchingEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.MATCHING_COINCIDENCIA_QUEUE)
    public void onCoincidenciaDetectada(MatchingCoincidenciaEventDTO evento) {
        log.info("Evento recibido: coincidencia {} con {}%",
                evento.getCoincidenciaId(), evento.getPorcentajeCoincidencia());
        emailService.enviarAvisoCoincidencia(evento);
    }
}
