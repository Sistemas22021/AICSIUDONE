package com.guardia.core.controller;

import com.guardia.core.dto.TipoDelitoDto;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.repository.TipoDelitoRepository;
import com.guardia.core.service.TipoDelitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor

@RestController
@RequestMapping("/api/tipos-delito")
public class TipoDelitoController {
    @Autowired
    private TipoDelitoService service;
    @Autowired
    private TipoDelitoRepository repository;

    @GetMapping("/list")
    public ResponseEntity<List<TipoDelitoDto>> obtenerTodos() {
        return ResponseEntity.ok(service.listarParaDesplegable());
    }

    @PostMapping("/delitofrontt")
    public ResponseEntity<String> nombreDelito(@RequestBody TipoDelitoDto delito) {
        System.out.println("El delito recibido: " + delito.nombre());
        return ResponseEntity.ok(service.obtenerNombreDelito(delito));
    }

    @PostMapping("/delitofront")
    public ResponseEntity<?> corroborarDelito(@RequestBody TipoDelitoDto delitoDto) {

        // 1. Verificamos en la tabla de Neon si el nombre existe
        boolean existeEnBaseDeDatos = repository.existsByNombreIgnoreCase(delitoDto.nombre().trim());

        if (existeEnBaseDeDatos) {
            System.out.println("Validación exitosa: '" + delitoDto.nombre() + "' existe en la tabla.");

            // Opcional: Obtener la lista completa para confirmarlo visualmente en el front
            return ResponseEntity.ok(repository.findAll());
        } else {
            System.out.println("Alerta: '" + delitoDto.nombre() + "' NO se encuentra en la base de datos.");

            // Retornamos un 404 o un mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El delito enviado no existe en nuestros registros.");
        }
    }

    /*@PostMapping("/delitofront")
    public ResponseEntity<?> corroborarDelito(@RequestBody TipoDelitoDto delitoDto) {

        // Imprime lo que buscas
        System.out.println("Buscando: '" + delitoDto.nombre() + "'");

        // Trae todo de la tabla para ver qué hay realmente
        List<TipoDelito> listaReal = repository.findAll();
        System.out.println("Contenido real en Neon:");
        listaReal.forEach(d -> System.out.println("-" + d.getNombre() + "-"));

        boolean existe = repository.existsByNombreIgnoreCase(delitoDto.nombre().trim());

        return ResponseEntity.ok(existe ? "Existe" : "No existe");
    }*/
}

