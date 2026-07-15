package com.guardia.core.service;

import com.guardia.core.dto.request.EscenaNegativaRequest;
import com.guardia.core.dto.response.EscenaNegativaResponse;

import java.util.List;

/**
 * Servicio para gestionar registros de escenas negativas (sin hallazgos).
 */
public interface EscenaNegativaService {
    EscenaNegativaResponse crear(EscenaNegativaRequest request);
    EscenaNegativaResponse obtenerPorId(Long id);
    List<EscenaNegativaResponse> obtenerTodos();
    List<EscenaNegativaResponse> obtenerPorEscena(Long escenaId);
    EscenaNegativaResponse actualizar(Long id, EscenaNegativaRequest request);
    void eliminar(Long id);
    EscenaNegativaResponse registrarResultadoNoEncontrado(Long id, String area, String observacion);
    EscenaNegativaResponse agregarObservacion(Long id, String observacion);
    boolean validarRegistro(Long id);
}
