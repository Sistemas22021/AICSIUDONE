package com.guardia.core.service;

import com.guardia.core.dto.request.TipoDelitoRequest;
import com.guardia.core.dto.response.TipoDelitoResponse;

import java.util.List;

/**
 * Servicio para gestionar tipos de delito y operaciones asociadas (CRUD, filtros).
 */
public interface TipoDelitoService {
    TipoDelitoResponse crear(TipoDelitoRequest request);
    TipoDelitoResponse obtenerPorId(Long id);
    List<TipoDelitoResponse> obtenerTodos();
    List<TipoDelitoResponse> obtenerQueRequierenSubtipo();
    TipoDelitoResponse actualizar(Long id, TipoDelitoRequest request);
    void eliminar(Long id);
}
