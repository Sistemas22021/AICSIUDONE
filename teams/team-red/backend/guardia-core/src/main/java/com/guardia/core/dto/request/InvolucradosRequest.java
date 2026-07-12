package com.guardia.core.dto.request;

import com.guardia.core.model.enums.TipoRol;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
/**
 * DTO de solicitud para involucrados dentro de un expediente.
 * Incluye datos personales, rol, fotografía y relación con el hecho.
 */
public class InvolucradosRequest {

    @NotBlank
    private String nombre;

    private String apellido;

    @NotBlank
    private String cedula;

    private String telefono;

    private String nacionalidad;

    private String direccion;

    private String fotografiaURL;

    private TipoRol rol;

    private String relacionConHecho;
}
