package com.guardia.core.service;

import com.guardia.core.dto.request.ActualizarFirmaConductualRequest;
import com.guardia.core.dto.request.RegistrarFirmaConductualRequest;
import com.guardia.core.dto.response.FirmaConductualResponse;

import java.util.List;

public interface FirmaConductualService {

    /**
     * Registra una nueva firma conductual asociada a un expediente.
     */
    FirmaConductualResponse registrar(RegistrarFirmaConductualRequest request);
    /**
     * Edita una firma conductual creando una nueva versión.
     */
    FirmaConductualResponse editar(Long firmaId,ActualizarFirmaConductualRequest request);

    /**
     * Obtiene la versión vigente de la firma conductual de un expediente.
     */
    FirmaConductualResponse obtenerActual(Long expedienteId);
    /**
     * Obtiene el historial completo de versiones de una firma conductual.
     */
    List<FirmaConductualResponse> obtenerHistorial(Long expedienteId);
    /**
     * Verifica si un expediente posee una firma conductual registrada.
     */
    boolean existeFirma(Long expedienteId);

}