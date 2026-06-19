# sanosysalvos-notificaciones

Este es el microservicio de notificaciones para la aplicación "Sanos y Salvos". Su responsabilidad principal es enviar notificaciones a los usuarios, como alertas por correo electrónico cuando se encuentra una posible coincidencia para su mascota perdida.

## Arquitectura

El servicio expone una API REST para recibir solicitudes de notificación. Internamente, utiliza `Spring Mail` para comunicarse con un servidor SMTP y enviar los correos.

Está diseñado para ser desacoplado y puede extenderse fácilmente para soportar otros tipos de notificaciones (Push, SMS, etc.).

## API Endpoints

### Enviar Alerta de Coincidencia

*   **POST** `/api/notificaciones/alerta-coincidencia`
*   **Descripción**: Recibe una solicitud para notificar a un usuario sobre una posible coincidencia.
*   **Cuerpo de la Petición (Request Body)**:

    ```json
    {
      "usuarioId": "string",
      "reporteCoincidenciaId": "string"
    }
    ```

*   **Respuesta Exitosa (200 OK)**:

    ```
    Solicitud de notificación de coincidencia recibida y procesándose.
    ```

## Cómo ejecutar localmente

1.  **Configuración**: Actualiza el archivo `src/main/resources/application.properties` con las credenciales de tu servidor SMTP.
2.  **Construir**: Ejecuta `mvn clean install` para compilar el proyecto y correr las pruebas.
3.  **Ejecutar**: Inicia la aplicación con `mvn spring-boot:run`.

## Cómo ejecutar con Docker

1.  Construye la imagen de Docker:
    `docker build -t sanosysalvos/notificaciones-service .`
2.  Ejecuta el contenedor:
    `docker run -p 8084:8084 -e SPRING_MAIL_USERNAME=tu_user -e SPRING_MAIL_PASSWORD=tu_pass sanosysalvos/notificaciones-service`
    *(Nota: Es mejor práctica manejar secretos con Docker Secrets o variables de entorno en tu orquestador)*