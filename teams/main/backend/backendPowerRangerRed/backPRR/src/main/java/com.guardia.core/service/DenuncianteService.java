package com.guardia.core.service;

import com.guardia.core.dto.request.DenuncianteRequest;
import com.guardia.core.dto.response.DenuncianteResponse;

import java.util.List;

/**
 * Servicio para gestión de denunciantes: CRUD y búsquedas por identificación.
 */
public interface DenuncianteService {
    DenuncianteResponse crear(DenuncianteRequest request);
    DenuncianteResponse obtenerPorId(Long id);
    DenuncianteResponse obtenerPorIdentificacion(String identificacion);
    List<DenuncianteResponse> obtenerTodos();
    DenuncianteResponse actualizar(Long id, DenuncianteRequest request);
    void eliminar(Long id);
    DenuncianteResponse registrarRelacion(Long id, String relacion);
}
