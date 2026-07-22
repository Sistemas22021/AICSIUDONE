package com.nexocriminal.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones. Convierte errores en respuestas JSON limpias
 * con el mensaje y el codigo HTTP correcto, en vez de un 500 generico.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Reglas de negocio violadas (placa repetida, no encontrado, etc.). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        // Si el mensaje dice "no encontrado/a", devolvemos 404; si no, 400.
        String msg = ex.getMessage() != null ? ex.getMessage() : "Solicitud invalida";
        HttpStatus status = msg.toLowerCase().contains("no encontrad")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("error", msg));
    }

    /** Violacion de integridad referencial (borrar algo que tiene dependencias). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error",
                "No se puede completar la operacion porque este registro esta vinculado a otros datos (por ejemplo, avistamientos o sucesos). Elimina primero esos registros relacionados."
        ));
    }
}