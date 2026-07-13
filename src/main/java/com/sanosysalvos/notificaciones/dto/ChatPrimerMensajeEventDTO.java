package com.sanosysalvos.notificaciones.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Contrato del evento que sanosysalvos-chat-services debe publicar
 * en la routing key "chat.mensaje.primero", únicamente cuando el mensaje
 * recibido es el PRIMERO de esa conversación (chatId).
 *
 * Ejemplo de payload JSON:
 * {
 *   "chatId": "chat-123",
 *   "reporteId": "reporte-456",
 *   "remitenteId": "user-1",
 *   "remitenteNombre": "Juan Pérez",
 *   "destinatarioId": "user-2",
 *   "destinatarioEmail": "maria@example.com",
 *   "destinatarioNombre": "María López",
 *   "contenidoMensaje": "Hola, creo que encontré a tu mascota...",
 *   "fechaEnvio": "2026-07-13T15:30:00Z"
 * }
 */
public class ChatPrimerMensajeEventDTO implements Serializable {

    private String chatId;
    private String reporteId;
    private String remitenteId;
    private String remitenteNombre;
    private String destinatarioId;
    private String destinatarioEmail;
    private String destinatarioNombre;
    private String contenidoMensaje;
    private Instant fechaEnvio;

    public ChatPrimerMensajeEventDTO() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getReporteId() {
        return reporteId;
    }

    public void setReporteId(String reporteId) {
        this.reporteId = reporteId;
    }

    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public String getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(String destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public void setDestinatarioEmail(String destinatarioEmail) {
        this.destinatarioEmail = destinatarioEmail;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public void setDestinatarioNombre(String destinatarioNombre) {
        this.destinatarioNombre = destinatarioNombre;
    }

    public String getContenidoMensaje() {
        return contenidoMensaje;
    }

    public void setContenidoMensaje(String contenidoMensaje) {
        this.contenidoMensaje = contenidoMensaje;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Instant fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    @Override
    public String toString() {
        return "ChatPrimerMensajeEventDTO{" +
                "chatId='" + chatId + '\'' +
                ", destinatarioEmail='" + destinatarioEmail + '\'' +
                '}';
    }
}
