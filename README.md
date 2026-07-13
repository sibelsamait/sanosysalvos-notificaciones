# sanosysalvos-notificaciones

Microservicio de notificaciones para "Sanos y Salvos". Escucha eventos de RabbitMQ y envía
correos electrónicos gratuitos vía SMTP (Gmail o Brevo) cuando:

1. **Se inicia un chat nuevo** — solo en el primer mensaje de una conversación.
2. **Se detecta una coincidencia de avistamiento con probabilidad ≥ 85%.**

También conserva el endpoint REST original para disparos manuales:
`POST /api/notificaciones/alerta-coincidencia`.

## Arquitectura

```
chat-service ────publica──▶ chat.mensaje.primero ────┐
                                                       ├──▶ sanosysalvos.notificaciones.exchange (topic)
matching-service ─publica─▶ matching.coincidencia.detectada ┘
                                                       │
                                          ┌────────────┴─────────────┐
                                          ▼                          ▼
                          notificaciones.chat.primer-mensaje.queue   notificaciones.matching.coincidencia.queue
                                          │                          │
                                          ▼                          ▼
                                  ChatEventListener          MatchingEventListener
                                          │                          │ (filtra >= 85%)
                                          └───────────┬──────────────┘
                                                       ▼
                                                  EmailService ──▶ SMTP (Gmail/Brevo) ──▶ 📧
```

Cada cola tiene su propia **Dead Letter Queue** (`*.dlq`). Si el envío de correo falla tras
3 reintentos (configurable), el mensaje se mueve a la DLQ en vez de perderse.

## Contrato de eventos (para chat-service y matching-service)

### 1. Primer mensaje de chat

- **Exchange:** `sanosysalvos.notificaciones.exchange` (topic)
- **Routing key:** `chat.mensaje.primero`
- **Cuándo publicar:** el chat-service debe publicar este evento **solo la primera vez**
  que se registra un mensaje para un `chatId` (esa lógica de "es el primero" vive en el
  chat-service, que es quien tiene la base de datos de mensajes).
- **Payload:**

```json
{
  "chatId": "chat-123",
  "reporteId": "reporte-456",
  "remitenteId": "user-1",
  "remitenteNombre": "Juan Pérez",
  "destinatarioId": "user-2",
  "destinatarioEmail": "maria@example.com",
  "destinatarioNombre": "María López",
  "contenidoMensaje": "Hola, creo que encontré a tu mascota...",
  "fechaEnvio": "2026-07-13T15:30:00Z"
}
```

> **Nota:** el chat-service no debería tener el email del usuario en su propia base de
> datos (regla de "database per service"). Debe resolverlo con una llamada HTTP interna al
> auth-service (o al servicio que gestione los datos de usuario) **antes** de publicar el
> evento, y adjuntarlo ya resuelto en `destinatarioEmail`.

### 2. Coincidencia de matching

- **Exchange:** `sanosysalvos.notificaciones.exchange` (topic)
- **Routing key:** `matching.coincidencia.detectada`
- **Cuándo publicar:** el matching-service puede publicar **todas** las coincidencias que
  calcule, sin importar el porcentaje. El filtro de negocio (`>= 85%`) se aplica dentro de
  este servicio (`EmailServiceImpl.UMBRAL_COINCIDENCIA`), así el umbral se administra en un
  solo lugar.
- **Payload** (`porcentajeCoincidencia` en escala 0-100):

```json
{
  "coincidenciaId": "match-789",
  "reporteMascotaPerdidaId": "reporte-111",
  "reporteMascotaEncontradaId": "reporte-222",
  "porcentajeCoincidencia": 91.5,
  "destinatarioId": "user-1",
  "destinatarioEmail": "juan@example.com",
  "destinatarioNombre": "Juan Pérez",
  "nombreMascota": "Firulais",
  "fechaDeteccion": "2026-07-13T15:30:00Z"
}
```

## Configurar el envío de correo gratuito

Elige una opción SMTP gratuita (ver `.env.example` para más detalle):

- **Gmail** (`smtp.gmail.com:587`): gratis, pero necesitas activar verificación en 2 pasos
  y generar una "Contraseña de aplicación" en https://myaccount.google.com/apppasswords —
  **nunca uses tu contraseña normal**. Gmail limita el volumen diario (~500 correos/día en
  cuentas normales), suficiente para desarrollo/demo.
- **Brevo** (ex Sendinblue) (`smtp-relay.brevo.com:587`): plan gratuito con 300
  correos/día, pensado para transaccionales, con menos fricción de spam que Gmail.

No se necesita ninguna tarjeta de crédito para ninguna de las dos opciones.

## Cómo ejecutar localmente

1. Copia `.env.example` a `.env` y completa tus credenciales SMTP.
2. Levanta RabbitMQ (y el servicio) con Docker:
   ```bash
   cd infra
   docker network create sanosysalvos-network || true
   docker compose --env-file ../.env up --build
   ```
   El panel de administración de RabbitMQ queda disponible en http://localhost:15672
   (usuario/clave por defecto: `guest`/`guest`).
3. O bien, si solo quieres levantar RabbitMQ y correr la app con Maven:
   ```bash
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management-alpine
   export MAIL_USERNAME=tu_correo@gmail.com
   export MAIL_PASSWORD=tu_contraseña_de_aplicacion
   mvn spring-boot:run
   ```

## Probar manualmente sin los otros microservicios

Desde el panel de RabbitMQ (http://localhost:15672 → Exchanges →
`sanosysalvos.notificaciones.exchange` → Publish message), publica un mensaje con:

- **Routing key:** `chat.mensaje.primero`
- **Payload:** el JSON de ejemplo de la sección de contrato de eventos.

O usando el endpoint REST:

```bash
curl -X POST http://localhost:8084/api/notificaciones/alerta-coincidencia \
  -H "Content-Type: application/json" \
  -d '{"mensaje": "Prueba de correo", "destinatario": "tu_correo@example.com"}'
```

## Pruebas unitarias

```bash
mvn clean test
```

Los tests cubren `EmailServiceImpl` (incluyendo el umbral de 85%) y ambos listeners con
Mockito, apuntando al mínimo de 60% de cobertura exigido por el proyecto (reporte generado
por JaCoCo en `target/site/jacoco/index.html`).

## Estructura del proyecto

```
src/main/java/com/sibelsamait/notificaciones/
├── NotificacionesApplication.java
├── config/RabbitMQConfig.java        # exchange, colas, DLQ, conversor JSON
├── controller/NotificacionController.java
├── dto/
│   ├── AlertaRequestDTO.java         # endpoint REST original
│   ├── ChatPrimerMensajeEventDTO.java
│   └── MatchingCoincidenciaEventDTO.java
├── exception/GlobalExceptionHandler.java
├── listener/
│   ├── ChatEventListener.java
│   └── MatchingEventListener.java
└── service/
    ├── EmailService.java
    └── impl/EmailServiceImpl.java    # umbral de 85% vive aquí
```
