package com.guardia.core.dto.request;

import com.guardia.core.model.enums.TipoRol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotBlank
    private String telefono;

    @NotBlank
    private String nacionalidad;

    @NotBlank
    private String direccion;

    private String fotografiaURL;

    @NotEmpty
    private List<TipoRol> Rol;



}
