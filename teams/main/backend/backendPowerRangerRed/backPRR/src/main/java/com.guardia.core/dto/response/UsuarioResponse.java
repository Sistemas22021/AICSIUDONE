package com.guardia.core.dto.response;

public record UsuarioResponse(
        Long id,
        String nombre,
        String identificacion,
        String correo
) {}
