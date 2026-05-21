package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DenuncianteRequest {

    @NotBlank(message = "La identificación del denunciante es obligatoria")
    private String identificacion; // sinónimo de cedula

    @NotBlank(message = "El nombre del denunciante es obligatorio")
    private String nombre;

    @NotBlank(message = "La nacionalidad del denunciante es obligatoria")
    private String nacionalidad;

    @NotBlank(message = "El número telefónico del denunciante es obligatorio")
    private String telefono; // sinónimo de numeroTelefono

    @NotBlank(message = "La dirección del denunciante es obligatoria")
    private String direccion;

    @NotBlank(message = "Relacion con el hecho es obligatoria")
    private String relacionConHecho; // sinónimo de relacionConCrimen

    // Compatibilidad: métodos con nombres alternativos usados en diferentes versiones
    public String getCedula() { return this.identificacion; }
    public String getNumeroTelefono() { return this.telefono; }
    public String getRelacionConCrimen() { return this.relacionConHecho; }

    // Accesores estilo 'record' que algunas clases usan (sin prefijo get)
    public String identificacion() { return this.identificacion; }
    public String nombre() { return this.nombre; }
    public String telefono() { return this.telefono; }
    public String nacionalidad() { return this.nacionalidad; }
    public String direccion() { return this.direccion; }
    public String relacionConHecho() { return this.relacionConHecho; }
}