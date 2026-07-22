package com.nexocriminal.vinculo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Modelo de dominio de Vinculo (relacion polimorfica entre dos nodos). POJO puro. */
public class Vinculo {

    private final Long id;
    private final String tipoOrigen;
    private final Long idOrigen;
    private final String tipoDestino;
    private final Long idDestino;
    private final String reglaDetectada;
    private final BigDecimal score;
    private final Boolean activo;
    private final LocalDateTime detectadoEn;

    public Vinculo(Long id, String tipoOrigen, Long idOrigen, String tipoDestino, Long idDestino,
                   String reglaDetectada, BigDecimal score, Boolean activo, LocalDateTime detectadoEn) {
        this.id = id;
        this.tipoOrigen = tipoOrigen;
        this.idOrigen = idOrigen;
        this.tipoDestino = tipoDestino;
        this.idDestino = idDestino;
        this.reglaDetectada = reglaDetectada;
        this.score = score;
        this.activo = activo;
        this.detectadoEn = detectadoEn;
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