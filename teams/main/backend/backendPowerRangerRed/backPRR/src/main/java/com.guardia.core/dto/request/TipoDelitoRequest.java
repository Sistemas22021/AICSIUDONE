package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TipoDelitoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "Debe indicar si requiere subtipo")
        Boolean requiereSubtipo
) {
    public String getNombre() { return this.nombre; }
    public String getDescripcion() { return this.descripcion; }
    public Boolean getRequiereSubtipo() { return this.requiereSubtipo; }
}
