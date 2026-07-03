package com.guardia.core.service;

import com.guardia.core.dto.request.FirmarPasoRequest;
import com.guardia.core.dto.response.FirmaChecklistResponse;

import java.util.List;

public interface FirmaChecklistService {

    FirmaChecklistResponse firmarPaso(Long pasoChecklistId, FirmarPasoRequest request);

    List<FirmaChecklistResponse> obtenerHistorialFirmas(Long escenaId);
}