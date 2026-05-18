package com.guardia.core.service;

import com.guardia.core.dto.request.EvidenciaRequest;
import com.guardia.core.dto.response.EvidenciaResponse;

import java.util.List;

public interface EvidenciaService {
    EvidenciaResponse crear(EvidenciaRequest request);
    EvidenciaResponse obtenerPorId(Long id);
    List<EvidenciaResponse> obtenerTodos();
    List<EvidenciaResponse> obtenerPorEscena(Long escenaId);
    EvidenciaResponse actualizar(Long id, EvidenciaRequest request);
    void eliminar(Long id);
    EvidenciaResponse asignarNumero(Long id, String numero);
    EvidenciaResponse firmarLevantamiento(Long id, Long investigadorId);
    boolean validarIntegridad(Long id);
}
