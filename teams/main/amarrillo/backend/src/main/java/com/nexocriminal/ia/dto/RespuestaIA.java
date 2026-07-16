package com.nexocriminal.ia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaIA {
    private String contenido;
    private String modelo;
    private Integer tokensEntrada;
    private Integer tokensSalida;
    private Long durationMs;
}