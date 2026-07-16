package com.nexocriminal.suceso.infrastructure.persistence;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.domain.vehiculo.VehiculoService;
import com.nexocriminal.suceso.domain.port.VehiculoReaderPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter del puerto de vehiculo para el modulo suceso. Para lectura usa el
 * VehiculoService viejo (conservado); para marcar robado, reusa su cambiarEstado.
 */
@Component("sucesoVehiculoReaderAdapter")
public class VehiculoReaderAdapter implements VehiculoReaderPort {

    private final VehiculoService vehiculoService;

    public VehiculoReaderAdapter(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Override
    public Optional<VehiculoData> findById(Long id) {
        if (id == null) return Optional.empty();
        try {
            var v = vehiculoService.obtener(id);
            return Optional.of(new VehiculoData(
                    v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                    v.getEstado() != null ? v.getEstado().name() : null));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public void marcarRobado(Long vehiculoId) {
        vehiculoService.cambiarEstado(vehiculoId, EstadoVehiculo.ROBADO);
    }
}