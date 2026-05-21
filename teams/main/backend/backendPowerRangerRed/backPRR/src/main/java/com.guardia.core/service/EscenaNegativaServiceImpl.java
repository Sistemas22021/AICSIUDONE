package com.guardia.core.service;

import com.guardia.core.dto.request.EscenaNegativaRequest;
import com.guardia.core.dto.response.EscenaNegativaResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.EscenaNegativa;
import com.guardia.core.repository.EscenaNegativaRepository;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.service.EscenaNegativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EscenaNegativaServiceImpl implements EscenaNegativaService {

    private final EscenaNegativaRepository escenaNegativaRepository;
    private final EscenaRepository escenaRepository;

    @Override
    public EscenaNegativaResponse crear(EscenaNegativaRequest request) {
        Escena escena = escenaRepository.findById(request.escenaId())
                .orElseThrow(() -> new ResourceNotFoundException("Escena", request.escenaId()));

        EscenaNegativa escenaNegativa = new EscenaNegativa();
        escenaNegativa.setElementoBuscado(request.elementoBuscado());
        escenaNegativa.setAreaInspeccionada(request.areaInspeccionada());
        escenaNegativa.setResultado(request.resultado());
        escenaNegativa.setObservacion(request.observacion());
        escenaNegativa.setEscena(escena);

        return toResponse(escenaNegativaRepository.save(escenaNegativa));
    }

    @Override
    @Transactional(readOnly = true)
    public EscenaNegativaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaNegativaResponse> obtenerTodos() {
        return escenaNegativaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscenaNegativaResponse> obtenerPorEscena(Long escenaId) {
        return escenaNegativaRepository.findByEscenaId(escenaId).stream().map(this::toResponse).toList();
    }

    @Override
    public EscenaNegativaResponse actualizar(Long id, EscenaNegativaRequest request) {
        EscenaNegativa en = findById(id);
        en.setElementoBuscado(request.elementoBuscado());
        en.setAreaInspeccionada(request.areaInspeccionada());
        en.setResultado(request.resultado());
        en.setObservacion(request.observacion());
        return toResponse(escenaNegativaRepository.save(en));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        escenaNegativaRepository.deleteById(id);
    }

    @Override
    public EscenaNegativaResponse registrarResultadoNoEncontrado(Long id, String area, String observacion) {
        EscenaNegativa en = findById(id);
        en.registrarResultadoNoEncontrado(area, observacion);
        return toResponse(escenaNegativaRepository.save(en));
    }

    @Override
    public EscenaNegativaResponse agregarObservacion(Long id, String observacion) {
        EscenaNegativa en = findById(id);
        en.agregarObservacion(observacion);
        return toResponse(escenaNegativaRepository.save(en));
    }

    @Override
    public boolean validarRegistro(Long id) {
        return findById(id).validarRegistro();
    }

    private EscenaNegativa findById(Long id) {
        return escenaNegativaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EscenaNegativa", id));
    }

    public EscenaNegativaResponse toResponse(EscenaNegativa en) {
        Long escenaId = en.getEscena() != null ? en.getEscena().getId() : null;
        return new EscenaNegativaResponse(en.getId(), en.getElementoBuscado(),
                en.getAreaInspeccionada(), en.getResultado(), en.getObservacion(), escenaId);
    }
}
