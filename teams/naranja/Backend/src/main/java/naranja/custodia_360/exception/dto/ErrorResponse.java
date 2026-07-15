package naranja.custodia_360.exception.dto;

import java.time.LocalDateTime;

/**
 * Representa la estructura estandarizada para las respuestas de error del backend.
 * Sustituye las respuestas genéricas del servidor por un JSON limpio y semántico.
 * * @param timestamp Fecha y hora exacta en la que ocurrió el fallo.
 * @param status    Código de estado HTTP numérico (ej. 400, 401, 502).
 * @param error     Nombre estandarizado del error HTTP (ej. "Unauthorized", "Bad Gateway").
 * @param message   Mensaje descriptivo en español con los detalles del fallo para el usuario o frontend.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
    /**
     * Constructor compacto para asegurar que el timestamp nunca sea nulo
     * y proporcionar valores por defecto si es necesario.
     */
    public ErrorResponse {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    /**
     * Constructor sobrecargado auxiliar para errores simples que no requieren
     * una lista detallada de fallos.
     */
    public ErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message);
    }
}