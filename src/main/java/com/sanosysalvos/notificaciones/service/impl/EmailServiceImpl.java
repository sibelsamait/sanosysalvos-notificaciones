package com.sanosysalvos.notificaciones.service.impl;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import com.sanosysalvos.notificaciones.dto.ChatPrimerMensajeEventDTO;
import com.sanosysalvos.notificaciones.dto.MatchingCoincidenciaEventDTO;
import com.sanosysalvos.notificaciones.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String remitente;
    private final String nombreApp;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${notificaciones.mail.remitente}") String remitente,
            @Value("${notificaciones.mail.nombre-app:Sanos y Salvos}") String nombreApp) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.nombreApp = nombreApp;
    }

    @Override
    public void enviarAlertaGenerica(AlertaRequestDTO request) {
        enviarHtml(
                request.getDestinatario(),
                "[" + nombreApp + "] Nueva alerta",
                "<p>" + escapar(request.getMensaje()) + "</p>"
        );
    }

    @Override
    public void enviarAvisoPrimerMensajeChat(ChatPrimerMensajeEventDTO evento) {
        if (evento.getDestinatarioEmail() == null || evento.getDestinatarioEmail().isBlank()) {
            log.warn("Evento de primer mensaje sin destinatarioEmail, chatId={}", evento.getChatId());
            return;
        }

        String asunto = "[" + nombreApp + "] Tienes un nuevo mensaje sobre tu mascota";
        String cuerpo = """
                <div style="font-family: Arial, sans-serif; line-height: 1.5;">
                    <h2>¡Hola %s!</h2>
                    <p><strong>%s</strong> te ha escrito por primera vez en la plataforma %s.</p>
                    <blockquote style="border-left: 3px solid #4CAF50; margin: 12px 0; padding-left: 12px; color: #444;">
                        %s
                    </blockquote>
                    <p>Ingresa a la aplicación para continuar la conversación.</p>
                </div>
                """.formatted(
                escapar(nombreOrDefault(evento.getDestinatarioNombre())),
                escapar(nombreOrDefault(evento.getRemitenteNombre())),
                nombreApp,
                escapar(evento.getContenidoMensaje())
        );

        enviarHtml(evento.getDestinatarioEmail(), asunto, cuerpo);
    }

    @Override
    public void enviarAvisoCoincidencia(MatchingCoincidenciaEventDTO evento) {
        if (evento.getDestinatarioEmail() == null || evento.getDestinatarioEmail().isBlank()) {
            log.warn("Evento de coincidencia sin destinatarioEmail, coincidenciaId={}", evento.getCoincidenciaId());
            return;
        }

        String asunto = "[" + nombreApp + "] ¡Posible coincidencia encontrada!";
        String cuerpo = """
                <div style="font-family: Arial, sans-serif; line-height: 1.5;">
                    <h2>¡Hola %s!</h2>
                    <p>Encontramos una coincidencia con un <strong>%.1f%%</strong> de probabilidad
                    para la mascota <strong>%s</strong>.</p>
                    <p>Ingresa a la aplicación para revisar los detalles y contactar al otro usuario.</p>
                </div>
                """.formatted(
                escapar(nombreOrDefault(evento.getDestinatarioNombre())),
                evento.getPorcentajeCoincidencia(),
                escapar(nombreOrDefault(evento.getNombreMascota()))
        );

        enviarHtml(evento.getDestinatarioEmail(), asunto, cuerpo);
    }

    private void enviarHtml(String destinatario, String asunto, String cuerpoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            mailSender.send(mensaje);
            log.info("Correo enviado a {}", destinatario);
        } catch (MessagingException e) {
            log.error("Error construyendo el correo para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("No se pudo construir el correo", e);
        } catch (MailException e) {
            log.error("Error enviando el correo a {}: {}", destinatario, e.getMessage());
            throw e;
        }
    }

    private String nombreOrDefault(String nombre) {
        return (nombre == null || nombre.isBlank()) ? "usuario" : nombre;
    }

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
