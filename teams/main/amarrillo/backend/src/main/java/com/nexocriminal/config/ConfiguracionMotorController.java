package com.nexocriminal.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Configuración del Motor", description = "Umbrales y parámetros configurables de las reglas del motor Red Thread")
@RestController
@RequestMapping("/api/v1/config/motor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConfiguracionMotorController {

    private final ConfiguracionMotorService service;

    @GetMapping
    public ConfiguracionMotor obtener() {
        return service.obtener();
    }

    @PutMapping
    public ConfiguracionMotor guardar(@RequestBody ConfiguracionMotor config) {
        return service.guardar(config);
    }

    @PostMapping("/default")
    public ConfiguracionMotor restaurarDefaults() {
        return service.restaurarDefaults();
    }
}