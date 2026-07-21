package com.guardia.core.service;

import com.guardia.core.dto.request.ExpedienteRequest;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.dto.response.VerificacionHashResponse;
import com.guardia.core.dto.response.ExpedienteActivoResponse;
import com.guardia.core.model.enums.EstadoExpediente;

import java.util.List;
import java.util.UUID;

/**
 * Servicio central para la gestión de expedientes: creación, sellado y verificación de integridad.
 */
public interface ExpedienteService {
    ExpedienteResponse crear(ExpedienteRequest request);
    ExpedienteResponse obtenerPorId(Long id);
    ExpedienteResponse obtenerPorFolio(String folio);
    List<ExpedienteResponse> obtenerTodos();
    List<ExpedienteResponse> obtenerPorEstado(EstadoExpediente estado);
    List<ExpedienteResponse> obtenerPorCreador(UUID usuarioId);
    ExpedienteResponse actualizar(Long id, ExpedienteRequest request);
    void eliminar(Long id);
    ExpedienteResponse sellar(Long id, UUID agenteSelladorId);
    ExpedienteResponse cambiarEstado(Long id, EstadoExpediente nuevoEstado);
    ExpedienteResponse asignarInvestigador(Long id, UUID investigadorId);
    ExpedienteResponse vincularEscena(Long id, Long escenaId);
    ExpedienteResponse asignarFechaHecho(Long id, String fecha);
    VerificacionHashResponse verificarIntegridad(Long id);
    boolean validarDatos(Long id);
    List<ExpedienteActivoResponse> obtenerParaPanel(String estatus, String sort);
}