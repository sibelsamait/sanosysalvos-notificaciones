package com.sanosysalvos.notificaciones.service;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import com.sanosysalvos.notificaciones.dto.ChatPrimerMensajeEventDTO;
import com.sanosysalvos.notificaciones.dto.MatchingCoincidenciaEventDTO;

public interface EmailService {

    void enviarAlertaGenerica(AlertaRequestDTO request);

    void enviarAvisoPrimerMensajeChat(ChatPrimerMensajeEventDTO evento);

    void enviarAvisoCoincidencia(MatchingCoincidenciaEventDTO evento);
}
