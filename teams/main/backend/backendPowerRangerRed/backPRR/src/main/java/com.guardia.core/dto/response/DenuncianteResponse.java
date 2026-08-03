package com.guardia.core.dto.response;

public record DenuncianteResponse(
        Long id,
        String nombre,
        String identificacion,
        String telefono,
        String nacionalidad,
        String direccion,
        String relacionConHecho
) {}
