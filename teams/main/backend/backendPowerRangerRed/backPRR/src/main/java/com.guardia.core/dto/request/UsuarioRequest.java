package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para crear o actualizar un usuario del sistema.
 * Campos: nombre, identificación, credenciales y correo.
 */
public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,

        @NotBlank(message = "Las credenciales son obligatorias")
        String credenciales,

        @NotBlank(message = "El correo es obligatorio")
        String correo
) {
    public String getNombre() { return this.nombre; }
    public String getIdentificacion() { return this.identificacion; }
    public String getCredenciales() { return this.credenciales; }
    public String getCorreo() { return this.correo; }
}
