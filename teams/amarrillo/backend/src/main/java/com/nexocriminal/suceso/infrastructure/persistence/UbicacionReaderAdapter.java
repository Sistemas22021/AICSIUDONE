package com.nexocriminal.suceso.infrastructure.persistence;

import com.nexocriminal.domain.ubicacion.UbicacionRepository;
import com.nexocriminal.suceso.domain.port.UbicacionReaderPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adapter del puerto de ubicacion para el modulo suceso. */
@Component("sucesoUbicacionReaderAdapter")
public class UbicacionReaderAdapter implements UbicacionReaderPort {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionReaderAdapter(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public Optional<UbicacionData> findById(Long id) {
        if (id == null) return Optional.empty();
        return ubicacionRepository.findById(id)
                .map(u -> new UbicacionData(
                        u.getId(),
                        u.getDireccion(),
                        u.getLatitud() != null ? u.getLatitud() : 0.0,
                        u.getLongitud() != null ? u.getLongitud() : 0.0,
                        u.getTipo() != null ? u.getTipo().name() : null));
    }
}