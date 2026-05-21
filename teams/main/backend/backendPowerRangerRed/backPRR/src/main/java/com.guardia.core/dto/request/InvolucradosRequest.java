package com.guardia.core.dto.request;

import com.guardia.core.model.enums.TipoRol;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class InvolucradosRequest {
    @NotBlank(message = "El nombre de la victima es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido de la victima es obligatorio")
    private String apellido;

    @NotBlank(message = "La cedula de la victima es obligatoria")
    private String cedula;

    private String telefono;

    private String nacionalidad;

    private String direccion;

    private String fotografiaURL;

    private List<TipoRol> Rol;



}
