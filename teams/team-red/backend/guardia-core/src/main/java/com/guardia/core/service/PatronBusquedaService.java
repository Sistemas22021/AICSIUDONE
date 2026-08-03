package com.guardia.core.service;

import com.guardia.core.dto.request.PatronBusquedaRequest;
import com.guardia.core.dto.response.PatronBusquedaResultado;

import java.util.List;

/**
 * Búsqueda de patrones delictivos combinando Modus Operandi (MO) validado y
 * firma conductual mediante similitud semántica (embeddings + pgvector), para
 * identificar series delictivas, posibles autores recurrentes y tendencias de
 * criminalidad (HU "Buscar patrones por MO y firma conductual").
 */
public interface PatronBusquedaService {

    /**
     * Ejecuta la búsqueda combinada. Los resultados vienen ordenados de forma
     * descendente por nivel de similitud estimado (CA4).
     */
    List<PatronBusquedaResultado> buscar(PatronBusquedaRequest request);
}