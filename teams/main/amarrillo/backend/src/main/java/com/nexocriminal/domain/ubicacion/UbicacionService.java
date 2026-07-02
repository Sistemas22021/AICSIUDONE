package com.nexocriminal.domain.ubicacion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UbicacionService {

    private final UbicacionRepository repository;

    public Ubicacion crear(Ubicacion u) {
        return repository.save(u);
    }

    @Transactional(readOnly = true)
    public List<Ubicacion> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Ubicacion obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ubicacion no encontrada: " + id));
    }

    public Ubicacion actualizar(Long id, Ubicacion datos) {
        Ubicacion existente = obtener(id);
        existente.setDireccion(datos.getDireccion());
        existente.setLatitud(datos.getLatitud());
        existente.setLongitud(datos.getLongitud());
        existente.setTipo(datos.getTipo());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public void marcarComoNodoSospechoso(Long id) {
        Ubicacion u = obtener(id);
        u.setNodoSospechoso(true);
        repository.save(u);
    }
}
