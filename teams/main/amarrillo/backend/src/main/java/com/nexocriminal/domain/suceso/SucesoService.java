package com.nexocriminal.domain.suceso;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.domain.vehiculo.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SucesoService {

    private final SucesoRepository repository;
    private final VehiculoService vehiculoService;

    public Suceso crear(Suceso suceso) {
        Suceso guardado = repository.save(suceso);
        // Si es robo, marcar el vehiculo como ROBADO
        if (suceso.getTipo() == TipoSuceso.ROBO_VEHICULO && suceso.getVehiculo() != null) {
            vehiculoService.cambiarEstado(suceso.getVehiculo().getId(), EstadoVehiculo.ROBADO);
        }
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Suceso> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Suceso obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suceso no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Suceso> listarPorTipo(TipoSuceso tipo) {
        return repository.findByTipo(tipo);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
