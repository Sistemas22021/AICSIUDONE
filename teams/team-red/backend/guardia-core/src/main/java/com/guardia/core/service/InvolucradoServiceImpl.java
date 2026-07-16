package com.guardia.core.service;

import com.guardia.core.dto.response.InvolucradoResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Involucrado;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.repository.InvolucradoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de InvolucradoService: lógica para CRUD y consultas por rol/expediente.
 */
public class InvolucradoServiceImpl implements InvolucradoService {

    private final InvolucradoRepository involucradoRepository;

    @Override
    @Transactional(readOnly = true)
    public InvolucradoResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvolucradoResponse> obtenerTodos() {
        return involucradoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvolucradoResponse> obtenerPorRol(TipoRol rol) {
        return involucradoRepository.findByRol(rol)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvolucradoResponse> obtenerPorExpediente(Long expedienteId) {
        return involucradoRepository.findByExpedienteId(expedienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        involucradoRepository.deleteById(id);
    }

    private Involucrado findById(Long id) {
        return involucradoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Involucrado", id));
    }

    private InvolucradoResponse toResponse(Involucrado i) {
        return new InvolucradoResponse(
                i.getId(),
                i.getNombre(),
                i.getIdentificacion(),
                i.getNumeroTelefono(),
                i.getNacionalidad(),
                i.getDireccion(),
                i.getRol(),
                i.getRelacionConHecho()
        );
    }
}