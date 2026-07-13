package com.sanosysalvos.notificaciones.service;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import com.sanosysalvos.notificaciones.dto.ChatPrimerMensajeEventDTO;
import com.sanosysalvos.notificaciones.dto.MatchingCoincidenciaEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionesService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesService.class);

    /** Umbral de negocio: solo se notifica una coincidencia igual o mayor a este porcentaje. */
    private static final double UMBRAL_COINCIDENCIA = 85.0;

    private final EmailService emailService;

    public NotificacionesService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Procesa un evento de coincidencia, aplicando reglas de negocio antes de notificar.
     * @param evento El DTO del evento de coincidencia.
     */
    public void procesarEventoCoincidencia(MatchingCoincidenciaEventDTO evento) {
        Double porcentaje = evento.getPorcentajeCoincidencia();

        if (porcentaje == null || porcentaje < UMBRAL_COINCIDENCIA) {
            log.info("Coincidencia {} descartada por no cumplir umbral. Porcentaje: {}%",
                    evento.getCoincidenciaId(), porcentaje);
            return;
        }
        log.info("Coincidencia {} cumple umbral ({}%). Enviando notificación.",
                evento.getCoincidenciaId(), porcentaje);
        emailService.enviarAvisoCoincidencia(evento);
    }

    /**
     * Procesa un evento de primer mensaje de chat y envía una notificación.
     * @param evento El DTO del evento de chat.
     */
    public void procesarEventoPrimerMensaje(ChatPrimerMensajeEventDTO evento) {
        log.info("Procesando evento de primer mensaje para el chat {}. Enviando notificación.", evento.getChatId());
        emailService.enviarAvisoPrimerMensajeChat(evento);
    }

    /**
     * Procesa una solicitud de alerta genérica desde el endpoint REST.
     * @param request El DTO de la solicitud de alerta.
     */
    public void enviarAlerta(AlertaRequestDTO request) {
        log.info("Procesando alerta genérica para {}.", request.getDestinatario());
        emailService.enviarAlertaGenerica(request);
    }
}
