package com.guardia.core.dto.response;

/**
 * DTO de respuesta para localizaciones con resumen legible y coordenadas.
 */
public record LocalizacionResponse(
        Long id,
        String municipio,
        String sector,
        String direccion,
        String referencia,
        Double latitud,
        Double longitud,
        String resumen
) {}
