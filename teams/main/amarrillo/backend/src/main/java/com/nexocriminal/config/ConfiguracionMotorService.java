package com.nexocriminal.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestiona la fila unica de configuracion del motor. Si no existe, la crea con
 * los valores fundamentales. Permite guardar cambios y restaurar defaults.
 */
@Service
@RequiredArgsConstructor
public class ConfiguracionMotorService {

    private final ConfiguracionMotorRepository repository;

    /** Obtiene la config; si no existe, la crea con defaults. */
    @Transactional
    public ConfiguracionMotor obtener() {
        return repository.findById(1L)
                .orElseGet(() -> repository.save(ConfiguracionMotor.defaults()));
    }

    /** Guarda los valores recibidos (forzando id=1). */
    @Transactional
    public ConfiguracionMotor guardar(ConfiguracionMotor nueva) {
        ConfiguracionMotor actual = obtener();
        actual.copiarValoresDe(nueva);
        return repository.save(actual);
    }

    /** Restaura los valores fundamentales. */
    @Transactional
    public ConfiguracionMotor restaurarDefaults() {
        ConfiguracionMotor actual = obtener();
        actual.copiarValoresDe(ConfiguracionMotor.defaults());
        return repository.save(actual);
    }
}