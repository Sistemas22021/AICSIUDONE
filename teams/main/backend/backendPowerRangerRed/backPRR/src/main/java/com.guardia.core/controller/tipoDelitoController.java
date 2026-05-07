package com.guardia.core.controller;

import com.guardia.core.model.tipoDelito;
import com.guardia.core.service.tipoDelitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-delito")
@RequiredArgsConstructor
// <-- Ya no necesitas @CrossOrigin aquí, el CorsConfig se encarga de todo
public class tipoDelitoController {

    private final tipoDelitoService service;

    @GetMapping
    public List<tipoDelito> obtenerTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public tipoDelito crear(@RequestBody tipoDelito delito) {
        return service.guardar(delito);
    }
}