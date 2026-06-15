package com.guardia.core.service;

import com.guardia.core.dto.request.VictimaRequest;
import com.guardia.core.dto.response.VictimaResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Involucrado;
import com.guardia.core.model.Victima;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.InvolucradoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación del servicio VictimaService.
 * Contiene la lógica de negocio para crear, actualizar y consultar víctimas.
 */
public class VictimaServiceImpl implements VictimaService {

    private final InvolucradoRepository involucradoRepository;
    private final ExpedienteRepository expedienteRepository;

    @Override
    public VictimaResponse crear(VictimaRequest request) {
        if (involucradoRepository.existsByIdentificacionAndRol(request.identificacion(), TipoRol.VICTIMA))
            throw new BusinessException("Ya existe una víctima con esa identificación.");

        Expediente expediente = expedienteRepository.findById(request.expedienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", request.expedienteId()));

        Involucrado victima = new Involucrado();
        victima.setNombre(request.nombre());
        victima.setIdentificacion(request.identificacion());
        victima.setNumeroTelefono(request.telefono());
        victima.setNacionalidad(request.nacionalidad());
        victima.setDireccion(request.direccion());
        victima.setExpediente(expediente);
        victima.setRol(TipoRol.VICTIMA);

        return toResponse(involucradoRepository.save(victima));
    }

    @Override
    @Transactional(readOnly = true)
    public VictimaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VictimaResponse> obtenerTodos() {

        return involucradoRepository
                .findByRol(TipoRol.VICTIMA)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VictimaResponse> obtenerPorExpediente(Long expedienteId) {

        return involucradoRepository
                .findByExpedienteIdAndRol(
                        expedienteId,
                        TipoRol.VICTIMA
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public VictimaResponse actualizar(Long id, VictimaRequest request) {

        Involucrado v = findById(id);

        v.setNombre(request.nombre());
        v.setNumeroTelefono(request.telefono());
        v.setNacionalidad(request.nacionalidad());
        v.setDireccion(request.direccion());

        return toResponse(
                involucradoRepository.save(v)
        );
    }

    @Override
    public void eliminar(Long id) {

        findById(id);

        involucradoRepository.deleteById(id);
    }

    @Override
    public boolean validarIdentificacion(Long id) {

        Involucrado v = findById(id);

        return v.getIdentificacion() != null
                && !v.getIdentificacion().isBlank();
    }

    private Involucrado findById(Long id) {
        Involucrado involucrado = involucradoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Victima", id));

        if (involucrado.getRol() != TipoRol.VICTIMA)
            throw new BusinessException(
                    "El involucrado no es una víctima."
            );

        return involucrado;
    }

    public VictimaResponse toResponse(Involucrado v) {

        Long expId =
                v.getExpediente() != null
                        ? v.getExpediente().getId()
                        : null;

        return new VictimaResponse(
                v.getId(),
                v.getNombre(),
                v.getIdentificacion(),
                v.getNumeroTelefono(),
                v.getNacionalidad(),
                v.getDireccion(),
                expId
        );
    }
}
//