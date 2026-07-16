package com.nexocriminal.alerta.infrastructure.web.dto;

import com.nexocriminal.alerta.domain.model.Alerta;
import com.nexocriminal.domain.alerta.EstadoAlerta;
import com.nexocriminal.domain.alerta.NivelRiesgo;
import com.nexocriminal.domain.alerta.TipoAlerta;

import java.time.LocalDateTime;

/** DTO de salida para alerta. */
public class AlertaResponse {

    private final Long id;
    private final TipoAlerta tipo;
    private final String titulo;
    private final String descripcion;
    private final NivelRiesgo nivelRiesgo;
    private final Long vinculoId;
    private final EstadoAlerta estado;
    private final LocalDateTime creadaEn;

    public AlertaResponse(Alerta a) {
        this.id = a.getId();
        this.tipo = a.getTipo();
        this.titulo = a.getTitulo();
        this.descripcion = a.getDescripcion();
        this.nivelRiesgo = a.getNivelRiesgo();
        this.vinculoId = a.getVinculoId();
        this.estado = a.getEstado();
        this.creadaEn = a.getCreadaEn();
    }

    public Long getId() { return id; }
    public TipoAlerta getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public NivelRiesgo getNivelRiesgo() { return nivelRiesgo; }
    public Long getVinculoId() { return vinculoId; }
    public EstadoAlerta getEstado() { return estado; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
}