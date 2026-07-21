package com.guardia.core.service;

import com.guardia.core.dto.request.EvidenciaRequest;
import com.guardia.core.dto.response.EvidenciaResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar evidencias: registro, asignación de número y verificación de integridad.
 */
public interface EvidenciaService {
    EvidenciaResponse crear(EvidenciaRequest request);
    EvidenciaResponse obtenerPorId(Long id);
    List<EvidenciaResponse> obtenerTodos();
    List<EvidenciaResponse> obtenerPorEscena(Long escenaId);
    EvidenciaResponse actualizar(Long id, EvidenciaRequest request);
    void eliminar(Long id);
    EvidenciaResponse asignarNumero(Long id, String numero);
    EvidenciaResponse firmarLevantamiento(Long id, UUID  investigadorId);
    boolean validarIntegridad(Long id);
    boolean verificarHash(Long id);
}
