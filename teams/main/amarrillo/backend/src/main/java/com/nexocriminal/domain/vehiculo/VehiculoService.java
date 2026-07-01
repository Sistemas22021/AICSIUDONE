package com.nexocriminal.domain.vehiculo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoService {

    private final VehiculoRepository repository;

    public Vehiculo crear(Vehiculo vehiculo) {
        if (repository.findByPlaca(vehiculo.getPlaca()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehiculo con esa placa");
        }
        return repository.save(vehiculo);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehiculo obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehiculo no encontrado: " + id));
    }

    public Vehiculo actualizar(Long id, Vehiculo datos) {
        Vehiculo existente = obtener(id);
        existente.setMarca(datos.getMarca());
        existente.setModelo(datos.getModelo());
        existente.setAnio(datos.getAnio());
        existente.setColor(datos.getColor());
        existente.setEstado(datos.getEstado());
        existente.setPropietario(datos.getPropietario());
        return repository.save(existente);
    }

    public Vehiculo cambiarEstado(Long id, EstadoVehiculo estado) {
        Vehiculo v = obtener(id);
        v.setEstado(estado);
        return repository.save(v);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listarPorEstado(EstadoVehiculo estado) {
        return repository.findByEstado(estado);
    }
}
