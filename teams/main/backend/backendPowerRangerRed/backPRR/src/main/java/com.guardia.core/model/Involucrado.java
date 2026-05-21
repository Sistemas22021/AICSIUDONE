package com.guardia.core.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Involucrado {
    private String nombre;
    private String identificacion;
    private String nacionalidad;
    private String numeroTelefono;
    private String direccion;
}