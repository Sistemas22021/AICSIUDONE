package com.guardia.core.service;

import com.guardia.core.dto.request.DenuncianteRequest;
import com.guardia.core.dto.response.DenuncianteResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Denunciante;
import com.guardia.core.repository.DenuncianteRepository;
import com.guardia.core.service.DenuncianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DenuncianteServiceImpl implements DenuncianteService {

    private final DenuncianteRepository denuncianteRepository;

    @Override
    public DenuncianteResponse crear(DenuncianteRequest request) {
        if (denuncianteRepository.existsByIdentificacion(request.identificacion()))
            throw new BusinessException("Ya existe un denunciante con esa identificación.");

        Denunciante denunciante = new Denunciante();
        denunciante.setNombre(request.nombre());
        denunciante.setIdentificacion(request.identificacion());
        denunciante.setTelefono(request.telefono());
        denunciante.setNacionalidad(request.nacionalidad());
        denunciante.setDireccion(request.direccion());
        denunciante.setRelacionConHecho(request.relacionConHecho());

        return toResponse(denuncianteRepository.save(denunciante));
    }

    @Override
    @Transactional(readOnly = true)
    public DenuncianteResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DenuncianteResponse obtenerPorIdentificacion(String identificacion) {
        return denuncianteRepository.findByIdentificacion(identificacion)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Denunciante con identificación " + identificacion + " no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DenuncianteResponse> obtenerTodos() {
        return denuncianteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public DenuncianteResponse actualizar(Long id, DenuncianteRequest request) {
        Denunciante d = findById(id);
        d.setNombre(request.nombre());
        d.setTelefono(request.telefono());
        d.setNacionalidad(request.nacionalidad());
        d.setDireccion(request.direccion());
        d.setRelacionConHecho(request.relacionConHecho());
        return toResponse(denuncianteRepository.save(d));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        denuncianteRepository.deleteById(id);
    }

    @Override
    public DenuncianteResponse registrarRelacion(Long id, String relacion) {
        Denunciante d = findById(id);
        d.registrarRelacion(relacion);
        return toResponse(denuncianteRepository.save(d));
    }

    private Denunciante findById(Long id) {
        return denuncianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Denunciante", id));
    }

    public DenuncianteResponse toResponse(Denunciante d) {
        return new DenuncianteResponse(d.getId(), d.getNombre(), d.getIdentificacion(),
                d.getTelefono(), d.getNacionalidad(), d.getDireccion(), d.getRelacionConHecho());
    }
}
