package com.guardia.core.service;

import com.guardia.core.dto.request.AprobarPropuestaMoRequest;
import com.guardia.core.dto.request.CorregirPropuestaMoRequest;
import com.guardia.core.dto.request.RechazarPropuestaMoRequest;
import com.guardia.core.dto.response.PropuestaModusOperandiResponse;

import java.util.List;

/**
 * Servicio de validación experta del MO propuesto por el sistema (HU3):
 * consulta de la propuesta vigente, historial y las tres acciones del
 * Analista (aprobar / corregir / rechazar).
 */
public interface PropuestaModusOperandiService {

    /** Propuesta vigente de un expediente (la que se muestra en su ficha). */
    PropuestaModusOperandiResponse obtenerVigentePorExpediente(Long expedienteId);

    /** Historial completo (todas las versiones) de un expediente, más reciente primero. */
    List<PropuestaModusOperandiResponse> historialPorExpediente(Long expedienteId);

    PropuestaModusOperandiResponse aprobar(Long propuestaId, AprobarPropuestaMoRequest request);

    PropuestaModusOperandiResponse corregir(Long propuestaId, CorregirPropuestaMoRequest request);

    PropuestaModusOperandiResponse rechazar(Long propuestaId, RechazarPropuestaMoRequest request);
}