package com.guardia.core.dto.response;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para una evidencia registrada.
 * Incluye metadatos como hash, investigador y timestamp de registro.
 */
public record EvidenciaResponse(
        Long id,
        String numeroItem,
        String tipo,
        String descripcion,
        Long escenaId,
        String hashIntegridad,
        LocalDateTime timestampRegistro,
        String investigadorNombre
) {}
