package com.guardia.core.dto.response;

/**
 * DTO de respuesta con resultado de la verificación de hash de un expediente.
 * Contiene folio, booleano de integridad, mensajes y hashes comparados.
 */
public record VerificacionHashResponse(
        Long expedienteId,
        String folio,
        boolean integro,
        String mensaje,
        String hashAlmacenado,
        String hashRecalculado
) {}