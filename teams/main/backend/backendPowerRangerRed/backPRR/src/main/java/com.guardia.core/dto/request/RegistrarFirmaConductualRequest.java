package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarFirmaConductualRequest {

    @NotNull(message = "El expediente es obligatorio")
    private Long expedienteId;

    @NotNull(message = "El analista responsable es obligatorio")
    private Long analistaId;

    private String comportamientoPreDelictivo;
    private String metodoAproximacion;
    private String metodoAtaque;
    private String comportamientoPostDelictivo;
    private String elementosDistintivos;
}
//Para el push