package com.guardia.core.service;

import com.guardia.core.dto.request.ModusOperandiRequest;
import com.guardia.core.dto.response.ModusOperandiResponse;

import java.util.List;

/**
 * Servicio para análisis y gestión de modus operandi (patrones de actuación).
 */
public interface ModusOperandiService {
    ModusOperandiResponse crear(ModusOperandiRequest request);
    ModusOperandiResponse obtenerPorId(Long id);
    List<ModusOperandiResponse> obtenerTodos();
    List<ModusOperandiResponse> buscarPorPatron(String patron);
    ModusOperandiResponse actualizar(Long id, ModusOperandiRequest request);
    void eliminar(Long id);
    ModusOperandiResponse vincularExpediente(Long modusId, Long expedienteId);
    ModusOperandiResponse desvincularExpediente(Long modusId, Long expedienteId);
    ModusOperandiResponse agregarPatron(Long id, String patron);
    double compararExpedientes(Long modusId, Long expedienteAId, Long expedienteBId);
}
