// NotificacionesServiceTest.java
package com.sanosysalvos.notificaciones.service;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionesServiceTest {

    private NotificacionesService notificacionesService;

    @BeforeEach
    void setUp() {
        notificacionesService = new NotificacionesService();
    }

    @Test
    void enviarAlerta_noException() {
        AlertaRequestDTO alerta = new AlertaRequestDTO("Prueba mensaje", "usuario@ejemplo.com");
        // Simplemente validar que no genera excepciones
        assertDoesNotThrow(() -> notificacionesService.enviarAlerta(alerta));
    }
}