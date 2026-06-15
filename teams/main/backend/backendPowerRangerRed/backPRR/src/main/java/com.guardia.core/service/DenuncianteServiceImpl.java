package com.guardia.core.service;

import com.guardia.core.dto.request.DenuncianteRequest;
import com.guardia.core.dto.response.DenuncianteResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Denunciante;
import com.guardia.core.model.Involucrado;
import com.guardia.core.model.enums.TipoRol;
import com.guardia.core.repository.DenuncianteRepository;
import com.guardia.core.repository.InvolucradoRepository;
import com.guardia.core.service.DenuncianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de DenuncianteService: contiene lógica para crear y buscar denunciantes.
 */
public class DenuncianteServiceImpl implements DenuncianteService {

    private final InvolucradoRepository involucradoRepository;

    @Override
    public DenuncianteResponse crear(DenuncianteRequest request) {
        if (involucradoRepository.existsByIdentificacionAndRol(request.identificacion(), TipoRol.DENUNCIANTE))
            throw new BusinessException("Ya existe un denunciante con esa identificación.");

        Involucrado denunciante = new Involucrado();

        denunciante.setNombre(request.nombre());
        denunciante.setIdentificacion(request.identificacion());
        denunciante.setNumeroTelefono(request.telefono());
        denunciante.setNacionalidad(request.nacionalidad());
        denunciante.setDireccion(request.direccion());
        denunciante.setRol(TipoRol.DENUNCIANTE);

        return toResponse(involucradoRepository.save(denunciante));
    }

    @Override
    @Transactional(readOnly = true)
    public DenuncianteResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DenuncianteResponse obtenerPorIdentificacion(String identificacion) {

        return involucradoRepository
                .findByIdentificacionAndRol(
                        identificacion,
                        TipoRol.DENUNCIANTE
                )
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Denunciante con identificación "
                                        + identificacion
                                        + " no encontrado."
                        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DenuncianteResponse> obtenerTodos() {

        return involucradoRepository
                .findByRol(TipoRol.DENUNCIANTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DenuncianteResponse actualizar(Long id, DenuncianteRequest request) {

        Involucrado d = findById(id);

        d.setNombre(request.nombre());
        d.setNumeroTelefono(request.telefono());
        d.setNacionalidad(request.nacionalidad());
        d.setDireccion(request.direccion());
        d.setRelacionConHecho(request.relacionConHecho());

        return toResponse(
                involucradoRepository.save(d)
        );
    }

    @Override
    public void eliminar(Long id) {

        findById(id);

        involucradoRepository.deleteById(id);
    }

    @Override
    public DenuncianteResponse registrarRelacion(Long id, String relacion) {

        Involucrado d = findById(id);

        d.setRelacionConHecho(relacion);

        return toResponse(
                involucradoRepository.save(d)
        );
    }

    private Involucrado findById(Long id) {

        Involucrado involucrado =
                involucradoRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Denunciante", id));

        if (involucrado.getRol() != TipoRol.DENUNCIANTE) {
            throw new ResourceNotFoundException("Denunciante", id);
        }

        return involucrado;
    }

    public DenuncianteResponse toResponse(Involucrado d) {

        return new DenuncianteResponse(
                d.getId(),
                d.getNombre(),
                d.getIdentificacion(),
                d.getNumeroTelefono(),
                d.getNacionalidad(),
                d.getDireccion(),
                d.getRelacionConHecho()
        );
    }
}
