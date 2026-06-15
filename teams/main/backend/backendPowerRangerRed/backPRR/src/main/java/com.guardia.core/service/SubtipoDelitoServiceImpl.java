package com.guardia.core.service;

import com.guardia.core.dto.request.SubtipoDelitoRequest;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.SubtipoDelito;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.repository.SubtipoDelitoRepository;
import com.guardia.core.repository.TipoDelitoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de SubtipoDelitoService: contiene lógica para la gestión de subtipos.
 */
public class SubtipoDelitoServiceImpl implements SubtipoDelitoService {

    private final SubtipoDelitoRepository subtipoDelitoRepository;
    private final TipoDelitoRepository tipoDelitoRepository;

    @Override
    public SubtipoDelitoResponse crear(SubtipoDelitoRequest request) {
        if (subtipoDelitoRepository.existsByNombreAndTipoDelitoId(request.nombre(), request.tipoDelitoId()))
            throw new BusinessException("Ya existe ese subtipo para el tipo de delito indicado.");

        TipoDelito tipo = tipoDelitoRepository.findById(request.tipoDelitoId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoDelito", request.tipoDelitoId()));

        SubtipoDelito subtipo = new SubtipoDelito();
        subtipo.setNombre(request.nombre());
        subtipo.setDescripcion(request.descripcion());
        subtipo.setTipoDelito(tipo);

        return toResponse(subtipoDelitoRepository.save(subtipo));
    }

    @Override
    @Transactional(readOnly = true)
    public SubtipoDelitoResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubtipoDelitoResponse> obtenerTodos() {
        return subtipoDelitoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubtipoDelitoResponse> obtenerPorTipoDelito(Long tipoDelitoId) {
        return subtipoDelitoRepository.findByTipoDelitoId(tipoDelitoId).stream().map(this::toResponse).toList();
    }

    @Override
    public SubtipoDelitoResponse actualizar(Long id, SubtipoDelitoRequest request) {
        SubtipoDelito subtipo = findById(id);
        subtipo.setNombre(request.nombre());
        subtipo.setDescripcion(request.descripcion());
        return toResponse(subtipoDelitoRepository.save(subtipo));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        subtipoDelitoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarCorrespondencia(Long subtipoId, Long tipoId) {
        SubtipoDelito subtipo = findById(subtipoId);
        return subtipo.getTipoDelito() != null && subtipo.getTipoDelito().getId().equals(tipoId);
    }

    private SubtipoDelito findById(Long id) {
        return subtipoDelitoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubtipoDelito", id));
    }

    public SubtipoDelitoResponse toResponse(SubtipoDelito s) {
        return new SubtipoDelitoResponse(s.getId(), s.getNombre(), s.getDescripcion(),
                s.getTipoDelito().getId(), s.getTipoDelito().getNombre());
    }
}
