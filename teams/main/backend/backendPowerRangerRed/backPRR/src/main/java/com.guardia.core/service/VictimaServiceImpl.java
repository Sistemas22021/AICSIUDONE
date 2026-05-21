package com.guardia.core.service;

import com.guardia.core.dto.request.VictimaRequest;
import com.guardia.core.dto.response.VictimaResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Victima;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.VictimaRepository;
import com.guardia.core.service.VictimaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VictimaServiceImpl implements VictimaService {

    private final VictimaRepository victimaRepository;
    private final ExpedienteRepository expedienteRepository;

    @Override
    public VictimaResponse crear(VictimaRequest request) {
        if (victimaRepository.existsByIdentificacion(request.identificacion()))
            throw new BusinessException("Ya existe una víctima con esa identificación.");

        Expediente expediente = expedienteRepository.findById(request.expedienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", request.expedienteId()));

        Victima victima = new Victima();
        victima.setNombre(request.nombre());
        victima.setIdentificacion(request.identificacion());
        victima.setTelefono(request.telefono());
        victima.setNacionalidad(request.nacionalidad());
        victima.setDireccion(request.direccion());
        victima.setExpediente(expediente);

        return toResponse(victimaRepository.save(victima));
    }

    @Override
    @Transactional(readOnly = true)
    public VictimaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VictimaResponse> obtenerTodos() {
        return victimaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VictimaResponse> obtenerPorExpediente(Long expedienteId) {
        return victimaRepository.findByExpedienteId(expedienteId).stream().map(this::toResponse).toList();
    }

    @Override
    public VictimaResponse actualizar(Long id, VictimaRequest request) {
        Victima v = findById(id);
        v.setNombre(request.nombre());
        v.setTelefono(request.telefono());
        v.setNacionalidad(request.nacionalidad());
        v.setDireccion(request.direccion());
        return toResponse(victimaRepository.save(v));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        victimaRepository.deleteById(id);
    }

    @Override
    public boolean validarIdentificacion(Long id) {
        return findById(id).validarIdentificacion();
    }

    private Victima findById(Long id) {
        return victimaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Victima", id));
    }

    public VictimaResponse toResponse(Victima v) {
        Long expId = v.getExpediente() != null ? v.getExpediente().getId() : null;
        return new VictimaResponse(v.getId(), v.getNombre(), v.getIdentificacion(),
                v.getTelefono(), v.getNacionalidad(), v.getDireccion(), expId);
    }
}
