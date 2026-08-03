package com.guardia.core.service;

import com.guardia.core.dto.request.LocalizacionRequest;
import com.guardia.core.dto.response.LocalizacionResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Localizacion;
import com.guardia.core.repository.LocalizacionRepository;
import com.guardia.core.service.LocalizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocalizacionServiceImpl implements LocalizacionService {

    private final LocalizacionRepository localizacionRepository;

    @Override
    public LocalizacionResponse crear(LocalizacionRequest request) {
        Localizacion loc = new Localizacion();
        loc.setMunicipio(request.municipio());
        loc.setSector(request.sector());
        loc.setDireccion(request.direccion());
        loc.setReferencia(request.referencia());
        loc.setLatitud(request.latitud());
        loc.setLongitud(request.longitud());
        return toResponse(localizacionRepository.save(loc));
    }

    @Override
    @Transactional(readOnly = true)
    public LocalizacionResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalizacionResponse> obtenerTodos() {
        return localizacionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public LocalizacionResponse actualizar(Long id, LocalizacionRequest request) {
        Localizacion loc = findById(id);
        loc.setMunicipio(request.municipio());
        loc.setSector(request.sector());
        loc.setDireccion(request.direccion());
        loc.setReferencia(request.referencia());
        loc.setLatitud(request.latitud());
        loc.setLongitud(request.longitud());
        return toResponse(localizacionRepository.save(loc));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        localizacionRepository.deleteById(id);
    }

    @Override
    public LocalizacionResponse registrarGPS(Long id, Double lat, Double lon) {
        Localizacion loc = findById(id);
        loc.registrarGPS(lat, lon);
        return toResponse(localizacionRepository.save(loc));
    }

    @Override
    public LocalizacionResponse registrarDireccionManual(Long id, String municipio, String sector,
                                                          String direccion, String referencia) {
        Localizacion loc = findById(id);
        loc.registrarDireccionManual(municipio, sector, direccion, referencia);
        return toResponse(localizacionRepository.save(loc));
    }

    @Override
    public boolean validarUbicacion(Long id) {
        return findById(id).validarUbicacion();
    }

    private Localizacion findById(Long id) {
        return localizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localizacion", id));
    }

    public LocalizacionResponse toResponse(Localizacion l) {
        return new LocalizacionResponse(l.getId(), l.getMunicipio(), l.getSector(),
                l.getDireccion(), l.getReferencia(), l.getLatitud(), l.getLongitud(),
                l.obtenerResumenUbicacion());
    }
}
