package com.nexocriminal.robo;

import com.nexocriminal.domain.suceso.Suceso;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Robo completo", description = "Registro transaccional de un robo: crea víctima, vehículo, suceso y testigos en una operación")
@RestController
@RequestMapping("/api/v1/robo-completo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoboCompletoController {

    private final RoboCompletoService service;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RoboCompletoRequest req) {
        try {
            Suceso creado = service.registrar(req);
            return ResponseEntity.ok(Map.of(
                    "sucesoId", creado.getId(),
                    "mensaje", "Robo registrado completo"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}