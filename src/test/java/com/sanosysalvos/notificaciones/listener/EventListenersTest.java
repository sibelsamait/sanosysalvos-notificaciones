package com.sanosysalvos.notificaciones.listener;

import com.sanosysalvos.notificaciones.dto.ChatPrimerMensajeEventDTO;
import com.sanosysalvos.notificaciones.dto.MatchingCoincidenciaEventDTO;
import com.sanosysalvos.notificaciones.service.EmailService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EventListenersTest {

    @Test
    void chatEventListener_delegaAlEmailService() {
        EmailService emailService = mock(EmailService.class);
        ChatEventListener listener = new ChatEventListener(emailService);
        ChatPrimerMensajeEventDTO evento = new ChatPrimerMensajeEventDTO();
        evento.setChatId("chat-1");

        listener.onPrimerMensaje(evento);

        verify(emailService).enviarAvisoPrimerMensajeChat(evento);
    }

    @Test
    void matchingEventListener_delegaAlEmailService() {
        EmailService emailService = mock(EmailService.class);
        MatchingEventListener listener = new MatchingEventListener(emailService);
        MatchingCoincidenciaEventDTO evento = new MatchingCoincidenciaEventDTO();
        evento.setCoincidenciaId("match-1");
        evento.setPorcentajeCoincidencia(90.0);

        listener.onCoincidenciaDetectada(evento);

        verify(emailService).enviarAvisoCoincidencia(evento);
    }
}
