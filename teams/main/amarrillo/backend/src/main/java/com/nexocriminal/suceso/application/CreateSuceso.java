package com.nexocriminal.suceso.application;

import com.nexocriminal.suceso.domain.model.Suceso;
import com.nexocriminal.suceso.domain.port.SucesoRepositoryPort;
import com.nexocriminal.suceso.domain.port.VehiculoReaderPort;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: crear un suceso. Si es un robo de vehiculo, marca el vehiculo
 * como ROBADO a traves del puerto de vehiculo.
 */
@Service
public class CreateSuceso {

    private final SucesoRepositoryPort repository;
    private final VehiculoReaderPort vehiculoReader;

    public CreateSuceso(SucesoRepositoryPort repository, VehiculoReaderPort vehiculoReader) {
        this.repository = repository;
        this.vehiculoReader = vehiculoReader;
    }

    public Suceso execute(Suceso suceso) {
        Suceso guardado = repository.save(suceso);
        if (guardado.esRoboConVehiculo()) {
            vehiculoReader.marcarRobado(guardado.getVehiculoId());
        }
        return guardado;
    }
}