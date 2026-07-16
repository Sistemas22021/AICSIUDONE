package com.guardia.core.service;

import com.guardia.core.dto.request.TipoDelitoRequest;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.dto.response.TipoDelitoResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.repository.TipoDelitoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de TipoDelitoService con lógica de persistencia y validaciones.
 */
public class TipoDelitoServiceImpl implements TipoDelitoService {

    private final TipoDelitoRepository tipoDelitoRepository;

    @Override
    public TipoDelitoResponse crear(TipoDelitoRequest request) {
        if (tipoDelitoRepository.existsByNombre(request.nombre()))
            throw new BusinessException("Ya existe un tipo de delito con ese nombre.");

        TipoDelito tipo = new TipoDelito();
        tipo.setNombre(request.nombre());
        tipo.setDescripcion(request.descripcion());
        tipo.setRequiereSubtipo(request.requiereSubtipo());
        tipo.setSubtipos(new ArrayList<>());

        return toResponse(tipoDelitoRepository.save(tipo));
    }

    @Override
    @Transactional(readOnly = true)
    public TipoDelitoResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDelitoResponse> obtenerTodos() {
        return tipoDelitoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoDelitoResponse> obtenerQueRequierenSubtipo() {
        return tipoDelitoRepository.findByRequiereSubtipo(true).stream().map(this::toResponse).toList();
    }

    @Override
    public TipoDelitoResponse actualizar(Long id, TipoDelitoRequest request) {
        TipoDelito tipo = findById(id);
        tipo.setNombre(request.nombre());
        tipo.setDescripcion(request.descripcion());
        tipo.setRequiereSubtipo(request.requiereSubtipo());
        return toResponse(tipoDelitoRepository.save(tipo));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        tipoDelitoRepository.deleteById(id);
    }

    private TipoDelito findById(Long id) {
        return tipoDelitoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoDelito", id));
    }

    public TipoDelitoResponse toResponse(TipoDelito t) {
        List<SubtipoDelitoResponse> subtipos = t.getSubtipos() == null ? List.of() :
                t.getSubtipos().stream()
                        .map(s -> new SubtipoDelitoResponse(s.getId(), s.getNombre(),
                                s.getDescripcion(), t.getId(), t.getNombre()))
                        .toList();
        return new TipoDelitoResponse(t.getId(), t.getNombre(), t.getDescripcion(),
                t.getRequiereSubtipo(), subtipos);
    }
}
