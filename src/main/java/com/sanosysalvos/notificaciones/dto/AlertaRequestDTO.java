package com.sanosysalvos.notificaciones.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO del endpoint REST original:
 * POST /api/notificaciones/alerta-coincidencia
 */
public class AlertaRequestDTO {

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;

    @NotBlank(message = "El destinatario no puede estar vacío")
    private String destinatario;

    public AlertaRequestDTO() {
    }

    public AlertaRequestDTO(String mensaje, String destinatario) {
        this.mensaje = mensaje;
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public String toString() {
        return "AlertaRequestDTO{" +
                "mensaje='" + mensaje + '\'' +
                ", destinatario='" + destinatario + '\'' +
                '}';
    }
}
