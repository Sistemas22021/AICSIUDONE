package com.guardia.core.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FirmaConductualResponse {

    private Long id;
    private Long expedienteId;
    private Long analistaId;
    private Integer version;
    private Boolean vigente;
    private LocalDateTime fechaRegistro;
    private String comportamientoPreDelictivo;
    private String metodoAproximacion;
    private String metodoAtaque;
    private String comportamientoPostDelictivo;
    private String elementosDistintivos;
}
//Para el push