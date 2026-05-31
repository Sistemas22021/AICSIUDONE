package com.guardia.core.dto.response;

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
