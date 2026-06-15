package com.guardia.core.service;

import com.guardia.core.dto.request.VictimaRequest;
import com.guardia.core.dto.response.VictimaResponse;

import java.util.List;

/**
 * Servicio de dominio para operaciones sobre víctimas.
 * Define las operaciones CRUD y consultas relacionadas con víctimas de expedientes.
 */
public interface VictimaService {
    VictimaResponse crear(VictimaRequest request);
    VictimaResponse obtenerPorId(Long id);
    List<VictimaResponse> obtenerTodos();
    List<VictimaResponse> obtenerPorExpediente(Long expedienteId);
    VictimaResponse actualizar(Long id, VictimaRequest request);
    void eliminar(Long id);
    boolean validarIdentificacion(Long id);
}
