package com.guardia.core.service;

import com.guardia.core.dto.request.LocalizacionRequest;
import com.guardia.core.dto.response.LocalizacionResponse;

import java.util.List;

/**
 * Servicio para gestionar localizaciones: registrar GPS, dirección manual y validaciones.
 */
public interface LocalizacionService {
    LocalizacionResponse crear(LocalizacionRequest request);
    LocalizacionResponse obtenerPorId(Long id);
    List<LocalizacionResponse> obtenerTodos();
    LocalizacionResponse actualizar(Long id, LocalizacionRequest request);
    void eliminar(Long id);
    LocalizacionResponse registrarGPS(Long id, Double lat, Double lon);
    LocalizacionResponse registrarDireccionManual(Long id, String municipio, String sector,
                                                   String direccion, String referencia);
    boolean validarUbicacion(Long id);
}
