package com.nexocriminal.suceso.domain.port;

import java.util.Optional;

/** Puerto de lectura de ubicaciones: datos minimos para anidar en el suceso. */
public interface UbicacionReaderPort {

    Optional<UbicacionData> findById(Long id);

    record UbicacionData(Long id, String direccion, double latitud, double longitud, String tipo) {}
}