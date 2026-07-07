package com.nexocriminal.alerta.domain.model;

import com.nexocriminal.domain.alerta.EstadoAlerta;
import com.nexocriminal.domain.alerta.NivelRiesgo;
import com.nexocriminal.domain.alerta.TipoAlerta;

import java.time.LocalDateTime;

/** Modelo de dominio de Alerta. POJO puro. */
public class Alerta {

    private final Long id;
    private final TipoAlerta tipo;
    private final String titulo;
    private final String descripcion;
    private final NivelRiesgo nivelRiesgo;
    private final Long vinculoId;
    private EstadoAlerta estado;
    private final LocalDateTime creadaEn;

    public Alerta(Long id, TipoAlerta tipo, String titulo, String descripcion,
                  NivelRiesgo nivelRiesgo, Long vinculoId, EstadoAlerta estado, LocalDateTime creadaEn) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivelRiesgo = nivelRiesgo;
        this.vinculoId = vinculoId;
        this.estado = estado;
        this.creadaEn = creadaEn;
    }

    /** Regla de negocio: cambia el estado de la alerta (desplegada, descartada, etc.). */
    public void cambiarEstado(EstadoAlerta nuevoEstado) {
        this.estado = nuevoEstado;
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