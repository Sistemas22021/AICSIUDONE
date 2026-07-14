package com.guardia.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Referencia liviana (folio + similitud) a un expediente recuperado por búsqueda
 * semántica al generar una {@link PropuestaModusOperandi} (HU2, CA3-CA4).
 * Se almacena como colección embebida dentro de la propuesta; no es una entidad
 * propia porque no necesita identidad ni ciclo de vida independiente.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpedienteSimilarMO {

    @Column(name = "expediente_relacionado_id")
    private Long expedienteId;

    @Column(name = "expediente_relacionado_folio", length = 60)
    private String folio;

    @Column(name = "similitud_porcentaje")
    private Double similitudPorcentaje;
}