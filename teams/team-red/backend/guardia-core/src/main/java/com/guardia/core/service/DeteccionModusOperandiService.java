package com.guardia.core.service;

import com.guardia.core.model.Expediente;

/**
 * Orquesta el pipeline de detección automática de Modus Operandi (HU2):
 * generación de embedding, búsqueda semántica de expedientes similares,
 * consulta al LLM y persistencia de la propuesta resultante.
 */
public interface DeteccionModusOperandiService {

    void analizarPatrones(Long expedienteId);

    double compararExpedientes(Expediente a, Expediente b);
}