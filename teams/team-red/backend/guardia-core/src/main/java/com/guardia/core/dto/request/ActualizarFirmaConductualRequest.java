package com.guardia.core.dto.request;

import lombok.Data;

@Data
public class ActualizarFirmaConductualRequest {

    private String comportamientoPreDelictivo;
    private String metodoAproximacion;
    private String metodoAtaque;
    private String comportamientoPostDelictivo;
    private String elementosDistintivos;
}
//Para el push