package com.guardia.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class tipoDelitoDto {
    @NotBlank(message = "El nombre del delito es obligatorio")
    private String nombre;
    private String descripcion;
}
