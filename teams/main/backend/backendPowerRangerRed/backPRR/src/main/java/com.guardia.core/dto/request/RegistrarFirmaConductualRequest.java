package com.guardia.core.dto.request;

import lombok.Data;

@Data
public class RegistrarFirmaConductualRequest {

    private Long expedienteId;
    private Long analistaId;
    private String comportamientoPreDelictivo;
    private String metodoAproximacion;
    private String metodoAtaque;
    private String comportamientoPostDelictivo;
    private String elementosDistintivos;
}