package com.guardia.core.dto.response;

public record VictimaResponse(
        Long id,
        String nombre,
        String identificacion,
        String telefono,
        String nacionalidad,
        String direccion,
        Long expedienteId
) {}
