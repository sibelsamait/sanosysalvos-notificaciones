package com.sanosysalvos.notificaciones.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Contrato del evento que sanosysalvos-matching-service debe publicar
 * en la routing key "matching.coincidencia.detectada" CADA VEZ que calcula
 * una coincidencia, sin importar el porcentaje.
 *
 * Este microservicio (notificaciones) es responsable de filtrar y enviar
 * el correo solo cuando porcentajeCoincidencia >= 85.0. Así el matching-service
 * no necesita conocer el umbral de negocio de las notificaciones.
 *
 * "porcentajeCoincidencia" se expresa en escala 0-100.
 *
 * Ejemplo de payload JSON:
 * {
 *   "coincidenciaId": "match-789",
 *   "reporteMascotaPerdidaId": "reporte-111",
 *   "reporteMascotaEncontradaId": "reporte-222",
 *   "porcentajeCoincidencia": 91.5,
 *   "destinatarioId": "user-1",
 *   "destinatarioEmail": "juan@example.com",
 *   "destinatarioNombre": "Juan Pérez",
 *   "nombreMascota": "Firulais",
 *   "fechaDeteccion": "2026-07-13T15:30:00Z"
 * }
 */
public class MatchingCoincidenciaEventDTO implements Serializable {

    private String coincidenciaId;
    private String reporteMascotaPerdidaId;
    private String reporteMascotaEncontradaId;
    private Double porcentajeCoincidencia;
    private String destinatarioId;
    private String destinatarioEmail;
    private String destinatarioNombre;
    private String nombreMascota;
    private Instant fechaDeteccion;

    public MatchingCoincidenciaEventDTO() {
    }

    public String getCoincidenciaId() {
        return coincidenciaId;
    }

    public void setCoincidenciaId(String coincidenciaId) {
        this.coincidenciaId = coincidenciaId;
    }

    public String getReporteMascotaPerdidaId() {
        return reporteMascotaPerdidaId;
    }

    public void setReporteMascotaPerdidaId(String reporteMascotaPerdidaId) {
        this.reporteMascotaPerdidaId = reporteMascotaPerdidaId;
    }

    public String getReporteMascotaEncontradaId() {
        return reporteMascotaEncontradaId;
    }

    public void setReporteMascotaEncontradaId(String reporteMascotaEncontradaId) {
        this.reporteMascotaEncontradaId = reporteMascotaEncontradaId;
    }

    public Double getPorcentajeCoincidencia() {
        return porcentajeCoincidencia;
    }

    public void setPorcentajeCoincidencia(Double porcentajeCoincidencia) {
        this.porcentajeCoincidencia = porcentajeCoincidencia;
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

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public Instant getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(Instant fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    @Override
    public String toString() {
        return "MatchingCoincidenciaEventDTO{" +
                "coincidenciaId='" + coincidenciaId + '\'' +
                ", porcentajeCoincidencia=" + porcentajeCoincidencia +
                ", destinatarioEmail='" + destinatarioEmail + '\'' +
                '}';
    }
}
