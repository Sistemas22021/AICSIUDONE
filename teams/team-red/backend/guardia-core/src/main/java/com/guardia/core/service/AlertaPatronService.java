package com.guardia.core.service;

import com.guardia.core.dto.request.DescartarAlertaRequest;
import com.guardia.core.dto.request.RevisarAlertaRequest;
import com.guardia.core.dto.response.AlertaPatronResponse;

import java.util.List;
import java.util.UUID;

public interface AlertaPatronService {

    void evaluarYGenerarAlerta(Long propuestaModusOperandiId);

    /** Panel del Guardia: todas las alertas del sistema, pendientes primero */
    List<AlertaPatronResponse> listarTodas();

    /** Bandeja de notificaciones del Investigador asignado a los expedientes relacionados  */
    List<AlertaPatronResponse> listarPorInvestigador(UUID investigadorId);

    /** Marca la alerta como revisada, registrando usuario y timestamp */
    AlertaPatronResponse marcarRevisada(Long alertaId, RevisarAlertaRequest request);

    /** Marca la alerta como descartada, registrando usuario y timestamp */
    AlertaPatronResponse marcarDescartada(Long alertaId, DescartarAlertaRequest request);
}