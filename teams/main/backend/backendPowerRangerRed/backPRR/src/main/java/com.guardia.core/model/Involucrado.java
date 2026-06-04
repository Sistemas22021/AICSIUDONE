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
//Inicio de las modificaciones.
//Por hacer:
// 1.Crear Involucrado como entidad.
// 2.Crear su tabla.
// 3.Crear repository.
// 4.Modificar Expediente.
