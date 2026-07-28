package com.guardia.core.service;

import com.guardia.core.dto.request.FirmaConductualRequest;
import com.guardia.core.dto.response.FirmaConductualResponse;

import java.util.List;

public interface FirmaConductualService {
    FirmaConductualResponse registrarONuevaVersion(Long expedienteId, FirmaConductualRequest request);
    FirmaConductualResponse obtenerVigente(Long expedienteId);
    List<FirmaConductualResponse> historial(Long expedienteId);
    List<FirmaConductualResponse> buscarPorTexto(String texto);
}
