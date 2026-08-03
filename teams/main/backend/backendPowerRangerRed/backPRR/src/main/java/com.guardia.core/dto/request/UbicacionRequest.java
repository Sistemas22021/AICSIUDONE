package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UbicacionRequest {
    @NotBlank
    private String municipio;

    @NotBlank
    private String sector;

    @NotBlank
    private String direccion;

    @NotBlank
    private String referencia;

    @NotNull
    private CoordenadasRequest coordenadas;
}
