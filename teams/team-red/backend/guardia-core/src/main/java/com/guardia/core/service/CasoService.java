package com.guardia.core.service;

import com.guardia.core.dto.request.CasoRequest;
import com.guardia.core.dto.response.CasoResponse;

import java.util.List;

public interface CasoService {
    CasoResponse crear(CasoRequest request);
    CasoResponse obtenerPorId(Long id);
    List<CasoResponse> obtenerTodos();
}