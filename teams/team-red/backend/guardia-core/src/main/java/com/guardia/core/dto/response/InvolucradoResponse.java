package com.guardia.core.dto.response;

import com.guardia.core.model.enums.TipoRol;

/**
 * DTO de respuesta para un involucrado (víctima, testigo, sospechoso).
 * Contiene rol, datos de contacto y relación con el hecho.
 */
public record InvolucradoResponse(
        Long id,
        String nombre,
        String identificacion,
        String numeroTelefono,
        String nacionalidad,
        String direccion,
        TipoRol rol,
        String relacionConHecho
) {}