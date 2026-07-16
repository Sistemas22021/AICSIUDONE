package com.nexocriminal.suceso.domain.port;

import java.util.Optional;

/** Puerto de lectura de vehiculos: datos minimos para anidar en el suceso. */
public interface VehiculoReaderPort {

    Optional<VehiculoData> findById(Long id);

    /** Cambia el estado de un vehiculo a ROBADO (al registrar un robo). */
    void marcarRobado(Long vehiculoId);

    record VehiculoData(Long id, String placa, String marca, String modelo, String estado) {}
}