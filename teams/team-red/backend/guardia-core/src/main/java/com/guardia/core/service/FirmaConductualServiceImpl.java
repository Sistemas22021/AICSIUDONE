package com.guardia.core.service;

import com.guardia.core.dto.request.FirmaConductualRequest;
import com.guardia.core.dto.response.FirmaConductualResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.FirmaConductual;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.FirmaConductualRepository;
import com.guardia.core.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Registra y versiona la firma conductual de un expediente (HU "Registrar
 * firma conductual del caso"). Cada llamada a registrarONuevaVersion crea una
 * fila nueva y marca la anterior como no vigente, conservando el historial
 * completo con analista y timestamp de cada versión.
 */
public class FirmaConductualServiceImpl implements FirmaConductualService {

    private final FirmaConductualRepository firmaConductualRepository;
    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public FirmaConductualResponse registrarONuevaVersion(Long expedienteId, FirmaConductualRequest request) {
        Expediente expediente = expedienteRepository.findById(expedienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", expedienteId));

        Usuario analista = usuarioRepository.findById(request.analistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.analistaId()));

        FirmaConductual nueva = FirmaConductual.builder()
                .expediente(expediente)
                .comportamientoPreDelictivo(request.comportamientoPreDelictivo())
                .metodoAproximacion(request.metodoAproximacion())
                .metodoAtaque(request.metodoAtaque())
                .comportamientoPostDelictivo(request.comportamientoPostDelictivo())
                .elementosDistintivos(request.elementosDistintivos())
                .analista(analista)
                .fechaRegistro(LocalDateTime.now())
                .vigente(true)
                .build();

        if (!nueva.tieneAlMenosUnCampo()) {
            throw new BusinessException(
                    "Debe completar al menos uno de los campos de la firma conductual.");
        }

        FirmaConductual vigenteActual = firmaConductualRepository
                .findByExpedienteIdAndVigenteTrue(expedienteId).orElse(null);

        nueva.setVersion(vigenteActual == null ? 1 : vigenteActual.getVersion() + 1);

        if (vigenteActual != null) {
            vigenteActual.setVigente(false);
            firmaConductualRepository.save(vigenteActual);
        }

        return toResponse(firmaConductualRepository.save(nueva));
    }

    @Override
    @Transactional(readOnly = true)
    public FirmaConductualResponse obtenerVigente(Long expedienteId) {
        return firmaConductualRepository.findByExpedienteIdAndVigenteTrue(expedienteId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay firma conductual registrada para el expediente " + expedienteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FirmaConductualResponse> historial(Long expedienteId) {
        return firmaConductualRepository.findByExpedienteIdOrderByVersionDesc(expedienteId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FirmaConductualResponse> buscarPorTexto(String texto) {
        return firmaConductualRepository.buscarPorTexto(texto)
                .stream().map(this::toResponse).toList();
    }

    private FirmaConductualResponse toResponse(FirmaConductual f) {
        return new FirmaConductualResponse(
                f.getId(),
                f.getExpediente().getId(),
                f.getExpediente().getFolio(),
                f.getVersion(),
                f.isVigente(),
                f.getComportamientoPreDelictivo(),
                f.getMetodoAproximacion(),
                f.getMetodoAtaque(),
                f.getComportamientoPostDelictivo(),
                f.getElementosDistintivos(),
                f.getAnalista().getId(),
                f.getAnalista().getFullName(),
                f.getFechaRegistro());
    }
}
