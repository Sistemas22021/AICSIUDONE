package com.guardia.core.service;

import com.guardia.core.dto.request.SubtipoDelitoRequest;
import com.guardia.core.dto.response.SubtipoDelitoResponse;

import java.util.List;

public interface SubtipoDelitoService {
    SubtipoDelitoResponse crear(SubtipoDelitoRequest request);
    SubtipoDelitoResponse obtenerPorId(Long id);
    List<SubtipoDelitoResponse> obtenerTodos();
    List<SubtipoDelitoResponse> obtenerPorTipoDelito(Long tipoDelitoId);
    SubtipoDelitoResponse actualizar(Long id, SubtipoDelitoRequest request);
    void eliminar(Long id);
    boolean validarCorrespondencia(Long subtipoId, Long tipoId);
}
