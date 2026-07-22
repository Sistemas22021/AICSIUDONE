package com.nexocriminal.vinculo.infrastructure.web.dto;

import com.nexocriminal.vinculo.domain.model.Vinculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** DTO de salida para vinculo. */
public class VinculoResponse {

    private final Long id;
    private final String tipoOrigen;
    private final Long idOrigen;
    private final String tipoDestino;
    private final Long idDestino;
    private final String reglaDetectada;
    private final BigDecimal score;
    private final Boolean activo;
    private final LocalDateTime detectadoEn;

    public VinculoResponse(Vinculo v) {
        this.id = v.getId();
        this.tipoOrigen = v.getTipoOrigen();
        this.idOrigen = v.getIdOrigen();
        this.tipoDestino = v.getTipoDestino();
        this.idDestino = v.getIdDestino();
        this.reglaDetectada = v.getReglaDetectada();
        this.score = v.getScore();
        this.activo = v.getActivo();
        this.detectadoEn = v.getDetectadoEn();
    }

    public Long getId() { return id; }
    public String getTipoOrigen() { return tipoOrigen; }
    public Long getIdOrigen() { return idOrigen; }
    public String getTipoDestino() { return tipoDestino; }
    public Long getIdDestino() { return idDestino; }
    public String getReglaDetectada() { return reglaDetectada; }
    public BigDecimal getScore() { return score; }
    public Boolean getActivo() { return activo; }
    public LocalDateTime getDetectadoEn() { return detectadoEn; }
}