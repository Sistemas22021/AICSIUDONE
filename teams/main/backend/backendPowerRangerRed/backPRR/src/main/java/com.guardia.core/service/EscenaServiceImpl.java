package com.guardia.core.service;

import com.guardia.core.dto.request.EscenaRequest;
import com.guardia.core.dto.response.EscenaResponse;
import com.guardia.core.dto.response.EscenaNegativaResponse;
import com.guardia.core.dto.response.EvidenciaResponse;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.service.EscenaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EscenaServiceImpl implements EscenaService {

    private final EscenaRepository escenaRepository;
    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public EscenaResponse crear(EscenaRequest request) {
        Expediente expediente = expedienteRepository.findById(request.expedienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", request.expedienteId()));

        Usuario investigador = usuarioRepository.findById(request.levantadaPorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.levantadaPorId()));

        Escena escena = Escena.builder()
                .expediente(expediente)
                .levantadaPor(investigador)
                .evidencias(new ArrayList<>())
                .escenasNegativas(new ArrayList<>())
                .build();

        return toResponse(escenaRepository.save(escena));
    }

    @Override
    @Transactional(readOnly = true)
    public EscenaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaResponse> obtenerTodos() {
        return escenaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaResponse> obtenerPorExpediente(Long expedienteId) {
        return escenaRepository.findByExpedienteId(expedienteId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaResponse> obtenerPorInvestigador(Long usuarioId) {
        return escenaRepository.findByLevantadaPorId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        escenaRepository.deleteById(id);
    }

    @Override
    public EscenaResponse iniciarChecklist(Long id) {
        Escena escena = findById(id);
        if ("INICIADO".equals(escena.getEstadoChecklist()) || "CERRADO".equals(escena.getEstadoChecklist()))
            throw new BusinessException("La escena ya fue iniciada o cerrada.");
        escena.iniciarChecklist();
        return toResponse(escenaRepository.save(escena));
    }

    @Override
    public EscenaResponse cerrar(Long id) {
        Escena escena = findById(id);
        if ("CERRADO".equals(escena.getEstadoChecklist()))
            throw new BusinessException("La escena ya está cerrada.");
        if (!"INICIADO".equals(escena.getEstadoChecklist()))
            throw new BusinessException("La escena debe estar iniciada antes de cerrarla.");
        escena.cerrar();
        return toResponse(escenaRepository.save(escena));
    }

    @Override
    public EscenaResponse bloquearEdicion(Long id) {
        Escena escena = findById(id);
        escena.bloquearEdicion();
        return toResponse(escenaRepository.save(escena));
    }

    @Override
    public boolean validarSecuencia(Long id) {
        return findById(id).validarSecuencia();
    }

    private Escena findById(Long id) {
        return escenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Escena", id));
    }

    public EscenaResponse toResponse(Escena e) {
        UsuarioResponse investigador = e.getLevantadaPor() == null ? null :
                new UsuarioResponse(e.getLevantadaPor().getId(), e.getLevantadaPor().getNombre(),
                        e.getLevantadaPor().getIdentificacion(), e.getLevantadaPor().getCorreo());

        Long expedienteId = e.getExpediente() != null ? e.getExpediente().getId() : null;

        List<EvidenciaResponse> evidencias = e.getEvidencias() == null ? List.of() :
                e.getEvidencias().stream()
                        .map(ev -> new EvidenciaResponse(ev.getId(), ev.getNumeroItem(),
                                ev.getTipo(), ev.getDescripcion(), e.getId()))
                        .toList();

        List<EscenaNegativaResponse> negativas = e.getEscenasNegativas() == null ? List.of() :
                e.getEscenasNegativas().stream()
                        .map(en -> new EscenaNegativaResponse(en.getId(), en.getElementoBuscado(),
                                en.getAreaInspeccionada(), en.getResultado(), en.getObservacion(), e.getId()))
                        .toList();

        return new EscenaResponse(e.getId(), e.getEstadoChecklist(), e.getInicioProceso(),
                e.getCierreProceso(), expedienteId, investigador, evidencias, negativas);
    }
}
