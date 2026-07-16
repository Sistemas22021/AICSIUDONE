package com.nexocriminal.domain.relacion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
class RelacionService {

    private final RelacionRepository repository;

    public Relacion crear(Relacion r) {
        return repository.save(r);
    }

    @Transactional(readOnly = true)
    public List<Relacion> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Relacion> listarPorPersona(Long personaId) {
        return repository.findByPersonaId(personaId);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}

@Tag(name = "Relaciones", description = "Relaciones sociales entre personas (familiar, amigo, laboral, etc.)")
@RestController
@RequestMapping("/api/v1/relaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class RelacionController {

    private final RelacionService service;

    @GetMapping
    public List<Relacion> listar() {
        return service.listar();
    }

    @GetMapping("/persona/{id}")
    public List<Relacion> porPersona(@PathVariable Long id) {
        return service.listarPorPersona(id);
    }

    @PostMapping
    public ResponseEntity<Relacion> crear(@Valid @RequestBody Relacion r) {
        return ResponseEntity.ok(service.crear(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
