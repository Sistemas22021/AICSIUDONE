package com.nexocriminal.fuentes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Orquesta la busqueda de datos externos.
 *
 * Cumple DIP: depende de la abstraccion EstrategiaBusqueda, no de
 * implementaciones concretas. No sabe si detras hay scraping, API o ambas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BuscadorPersonasService {

    private final EstrategiaBusqueda estrategia;

    public DatosPersonaExterna buscarPorCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return DatosPersonaExterna.noEncontrado(cedula, "VALIDACION");
        }
        log.info("[BUSCADOR] Estrategia activa: {}", estrategia.descripcion());
        return estrategia.buscar(cedula);
    }

    public String estrategiaActiva() {
        return estrategia.descripcion();
    }
}