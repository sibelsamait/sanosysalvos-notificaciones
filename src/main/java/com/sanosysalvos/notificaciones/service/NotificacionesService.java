// NotificacionesService.java
package com.sanosysalvos.notificaciones.service;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionesService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionesService.class);

    public void enviarAlerta(AlertaRequestDTO alerta) {
        // Simulación del envío de alerta
        logger.info("Enviando alerta al destinatario: {} con mensaje: {}", alerta.getDestinatario(), alerta.getMensaje());

        // Aquí se podría agregar lógica futura, p.ej. integración con sistemas externos.
    }
}