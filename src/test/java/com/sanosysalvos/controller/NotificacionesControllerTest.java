// NotificacionesControllerTest.java
package com.sanosysalvos.notificaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import com.sanosysalvos.notificaciones.service.NotificacionesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class NotificacionesControllerTest {

    private MockMvc mockMvc;
    private NotificacionesService notificacionesService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        notificacionesService = Mockito.mock(NotificacionesService.class);
        NotificacionesController controller = new NotificacionesController(notificacionesService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void enviarAlerta_endpointReturnsAccepted() throws Exception {
        AlertaRequestDTO alerta = new AlertaRequestDTO("Mensaje test", "test@ejemplo.com");
        String json = objectMapper.writeValueAsString(alerta);

        mockMvc.perform(post("/api/notificaciones/alerta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Alerta enviada correctamente"));

        Mockito.verify(notificacionesService, times(1)).enviarAlerta(any(AlertaRequestDTO.class));
    }
}