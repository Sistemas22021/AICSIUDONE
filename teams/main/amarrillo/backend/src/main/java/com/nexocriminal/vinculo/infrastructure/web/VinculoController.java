package com.nexocriminal.vinculo.infrastructure.web;

import com.nexocriminal.vinculo.application.ListVinculosActivos;
import com.nexocriminal.vinculo.application.ListVinculosPorNodo;
import com.nexocriminal.vinculo.infrastructure.web.dto.VinculoResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Adapter de entrada REST para vinculo (solo lectura). */
@RestController
@RequestMapping("/api/v1/vinculos")
@CrossOrigin(origins = "*")
public class VinculoController {

    private final ListVinculosActivos listActivos;
    private final ListVinculosPorNodo listPorNodo;

    public VinculoController(ListVinculosActivos listActivos, ListVinculosPorNodo listPorNodo) {
        this.listActivos = listActivos;
        this.listPorNodo = listPorNodo;
    }

    @GetMapping
    public List<VinculoResponse> listar() {
        return listActivos.execute().stream().map(VinculoResponse::new).toList();
    }

    @GetMapping("/nodo/{tipo}/{id}")
    public List<VinculoResponse> porNodo(@PathVariable String tipo, @PathVariable Long id) {
        return listPorNodo.execute(tipo, id).stream().map(VinculoResponse::new).toList();
    }
}