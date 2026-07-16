package com.nexocriminal.modus;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Modus Operandi", description = "Catálogo de modus operandi para clasificar sucesos")
@RestController
@RequestMapping("/api/v1/modus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ModusOperandiController {

    private final ModusOperandiService service;

    /** Para el dropdown: solo los activos. */
    @GetMapping
    public List<ModusOperandi> listar() {
        return service.listarActivos();
    }

    /** Para la pantalla de gestión: todos, incluso inactivos. */
    @GetMapping("/todos")
    public List<ModusOperandi> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public ModusOperandi crear(@RequestBody ModusOperandi m) {
        return service.crear(m);
    }

    @PutMapping("/{id}")
    public ModusOperandi actualizar(@PathVariable Long id, @RequestBody ModusOperandi m) {
        return service.actualizar(id, m);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}