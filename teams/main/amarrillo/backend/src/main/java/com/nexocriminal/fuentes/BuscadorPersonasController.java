package com.nexocriminal.fuentes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/fuentes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuscadorPersonasController {

    private final BuscadorPersonasService service;

    /** Busca datos externos de una persona por cedula. */
    @GetMapping("/persona/{cedula}")
    public DatosPersonaExterna buscar(@PathVariable String cedula) {
        return service.buscarPorCedula(cedula);
    }

    /** Indica que estrategia de fuentes esta activa. */
    @GetMapping("/estado")
    public Map<String, String> estado() {
        return Map.of("estrategiaActiva", service.estrategiaActiva());
    }
}