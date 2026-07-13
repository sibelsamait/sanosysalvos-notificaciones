package com.sanosysalvos.notificaciones.controller;

import com.sanosysalvos.notificaciones.dto.AlertaRequestDTO;
import com.sanosysalvos.notificaciones.service.NotificacionesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {

    private final NotificacionesService notificacionesService;
    private final EmailService emailService;

    public NotificacionesController(NotificacionesService notificacionesService) {
        this.notificacionesService = notificacionesService;
    }

    public NotificacionesController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/alerta")
    public ResponseEntity<String> enviarAlerta(@RequestBody AlertaRequestDTO alerta) {
        notificacionesService.enviarAlerta(alerta);
        return new ResponseEntity<>("Alerta enviada correctamente", HttpStatus.ACCEPTED);
    }

    @PostMapping("/alerta-coincidencia")
    public ResponseEntity<String> enviarAlertaCoincidencia(@Valid @RequestBody AlertaRequestDTO request) {
        emailService.enviarAlertaGenerica(request);
        return ResponseEntity.ok("Solicitud de notificación de coincidencia recibida y procesándose.");
    }
}