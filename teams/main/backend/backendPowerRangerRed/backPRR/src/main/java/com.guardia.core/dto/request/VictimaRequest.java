package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para registrar una víctima en un expediente.
 * Incluye nombre, identificación, contacto y referencia al expediente.
 */
public record VictimaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La identificación es obligatoria")
        String identificacion,

        String telefono,
        String nacionalidad,
        String direccion,

        @NotNull(message = "El expediente es obligatorio")
        Long expedienteId
) {
    // Compatibility getters (some services use getX() style)
    public String getNombre() { return this.nombre; }
    public String getIdentificacion() { return this.identificacion; }
    public String getCedula() { return this.identificacion; } // alias used in older code
    public String getTelefono() { return this.telefono; }
    public String getNacionalidad() { return this.nacionalidad; }
    public String getDireccion() { return this.direccion; }
    public Long getExpedienteId() { return this.expedienteId; }
}
