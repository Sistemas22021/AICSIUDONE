package com.guardia.core.dto.response;

import java.time.LocalDateTime;

/**
 * Resultado individual de la búsqueda de patrones por MO validado y/o firma
 * conductual (HU "Buscar patrones por MO y firma conductual", CA3).
 * Expone únicamente los campos requeridos por la HU: folio, tipo de delito,
 * fecha del hecho, nivel de similitud estimado y el investigador asignado.
 */
public record PatronBusquedaResultado(
        Long expedienteId,
        String folio,
        String tipoDelito,
        LocalDateTime fechaHecho,
        Double similitudPorcentaje,
        String investigadorAsignado
) {}
