package com.guardia.core.dto.request;

public record LocalizacionRequest(
        String municipio,
        String sector,
        String direccion,
        String referencia,
        Double latitud,
        Double longitud
) {}
