package com.guardia.core.dto.response;

/**
 * DTO de respuesta para cada expediente recuperado por similitud vectorial
 * dentro de una propuesta de Modus Operandi (HU2).
 */
public record ExpedienteSimilarResponse(
        Long expedienteId,
        String folio,
        Double similitudPorcentaje
) {}