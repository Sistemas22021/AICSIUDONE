package equipoBlanco.com.prison_service.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status;

        if (message != null && message.contains("Ya existe")) {
            status = HttpStatus.CONFLICT;
        } else if (message != null && (message.contains("no encontrado") || message.contains("no encontrada"))) {
            status = HttpStatus.NOT_FOUND;
        } else if (message != null && message.contains("llena")) {
            status = HttpStatus.BAD_REQUEST;
        } else if (message != null && message.contains("ya tiene celda")) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(Map.of("error", message != null ? message : "Error interno del servidor"));
    }
}
