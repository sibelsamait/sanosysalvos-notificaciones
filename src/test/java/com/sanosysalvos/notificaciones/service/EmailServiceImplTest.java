package com.sanosysalvos.notificaciones.service;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import com.sanosysalvos.notificaciones.dto.ChatPrimerMensajeEventDTO;
import com.sanosysalvos.notificaciones.dto.MatchingCoincidenciaEventDTO;
import com.sanosysalvos.notificaciones.service.impl.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    private JavaMailSender mailSender;
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));
        emailService = new EmailServiceImpl(mailSender, "no-reply@sanosysalvos.com", "Sanos y Salvos");
    }

    @Test
    void enviarAlertaGenerica_envíaCorreo() {
        AlertaRequestDTO request = new AlertaRequestDTO("Mensaje de prueba", "destino@example.com");

        emailService.enviarAlertaGenerica(request);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoPrimerMensajeChat_conEmailValido_envíaCorreo() {
        ChatPrimerMensajeEventDTO evento = new ChatPrimerMensajeEventDTO();
        evento.setChatId("chat-1");
        evento.setDestinatarioEmail("maria@example.com");
        evento.setDestinatarioNombre("María");
        evento.setRemitenteNombre("Juan");
        evento.setContenidoMensaje("Hola, encontré a tu mascota");
        evento.setFechaEnvio(Instant.now());

        emailService.enviarAvisoPrimerMensajeChat(evento);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoPrimerMensajeChat_sinEmail_noEnvíaCorreo() {
        ChatPrimerMensajeEventDTO evento = new ChatPrimerMensajeEventDTO();
        evento.setChatId("chat-2");
        evento.setDestinatarioEmail(null);

        emailService.enviarAvisoPrimerMensajeChat(evento);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoCoincidencia_porcentajeMayorAlUmbral_envíaCorreo() {
        MatchingCoincidenciaEventDTO evento = new MatchingCoincidenciaEventDTO();
        evento.setCoincidenciaId("match-1");
        evento.setPorcentajeCoincidencia(91.5);
        evento.setDestinatarioEmail("juan@example.com");
        evento.setDestinatarioNombre("Juan");
        evento.setNombreMascota("Firulais");
        evento.setFechaDeteccion(Instant.now());

        emailService.enviarAvisoCoincidencia(evento);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoCoincidencia_porcentajeIgualAlUmbral_envíaCorreo() {
        MatchingCoincidenciaEventDTO evento = new MatchingCoincidenciaEventDTO();
        evento.setCoincidenciaId("match-2");
        evento.setPorcentajeCoincidencia(85.0);
        evento.setDestinatarioEmail("juan@example.com");

        emailService.enviarAvisoCoincidencia(evento);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoCoincidencia_porcentajeMenorAlUmbral_noEnvíaCorreo() {
        MatchingCoincidenciaEventDTO evento = new MatchingCoincidenciaEventDTO();
        evento.setCoincidenciaId("match-3");
        evento.setPorcentajeCoincidencia(60.0);
        evento.setDestinatarioEmail("juan@example.com");

        emailService.enviarAvisoCoincidencia(evento);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoCoincidencia_sinPorcentaje_noEnvíaCorreo() {
        MatchingCoincidenciaEventDTO evento = new MatchingCoincidenciaEventDTO();
        evento.setCoincidenciaId("match-4");
        evento.setPorcentajeCoincidencia(null);
        evento.setDestinatarioEmail("juan@example.com");

        emailService.enviarAvisoCoincidencia(evento);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void enviarAvisoCoincidencia_conPorcentajeAlto_peroSinEmail_noEnvíaCorreo() {
        MatchingCoincidenciaEventDTO evento = new MatchingCoincidenciaEventDTO();
        evento.setCoincidenciaId("match-5");
        evento.setPorcentajeCoincidencia(95.0);
        evento.setDestinatarioEmail(" ");

        emailService.enviarAvisoCoincidencia(evento);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
