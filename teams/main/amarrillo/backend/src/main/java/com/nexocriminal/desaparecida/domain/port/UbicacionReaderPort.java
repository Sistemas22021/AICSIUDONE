package com.nexocriminal.desaparecida.domain.port;

import java.util.Optional;

/** Puerto de lectura de ubicaciones para anidar la ultima ubicacion. */
public interface UbicacionReaderPort {

    Optional<UbicacionData> findById(Long id);

    record UbicacionData(Long id, String direccion, double latitud, double longitud, String tipo) {}
}