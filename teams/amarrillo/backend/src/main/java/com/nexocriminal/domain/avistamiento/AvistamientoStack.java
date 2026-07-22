package com.nexocriminal.domain.avistamiento;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Repository
interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
    List<Avistamiento> findByVehiculoId(Long vehiculoId);
}

@Service
@RequiredArgsConstructor
@Transactional
class AvistamientoService {
    private final AvistamientoRepository repository;

    public Avistamiento crear(Avistamiento a) { return repository.save(a); }

    @Transactional(readOnly = true)
    public List<Avistamiento> listar() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public List<Avistamiento> porVehiculo(Long id) { return repository.findByVehiculoId(id); }
}

@RestController
@RequestMapping("/api/v1/avistamientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class AvistamientoController {

    private final AvistamientoService service;

    @GetMapping
    public List<Avistamiento> listar() {
        return service.listar();
    }

    @GetMapping("/vehiculo/{id}")
    public List<Avistamiento> porVehiculo(@PathVariable Long id) {
        return service.porVehiculo(id);
    }

    @PostMapping
    public ResponseEntity<Avistamiento> crear(@Valid @RequestBody Avistamiento a) {
        return ResponseEntity.ok(service.crear(a));
    }
}
