package com.guardia.core.controller;

import com.guardia.core.dto.request.PatronBusquedaRequest;
import com.guardia.core.dto.response.PatronBusquedaResultado;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.PatronBusquedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Búsqueda de patrones delictivos (series, autores recurrentes, tendencias)
 * combinando MO validado y/o firma conductual mediante similitud semántica.
 * Ruta principal: /api/v1/patrones (HU "Buscar patrones por MO y firma
 * conductual", pensada para el Analista Criminal / Investigador de Campo).
 */
@RestController
@RequestMapping("/api/v1/patrones")
@RequiredArgsConstructor
public class PatronBusquedaController {

    private final PatronBusquedaService patronBusquedaService;

    /**
     * CA1: {@code textoMO} y {@code textoFirmaConductual} son ambos opcionales
     * pero se exige al menos uno (validado en el servicio). CA3/CA4: cada
     * resultado trae folio, tipo de delito, fecha, similitud e investigador
     * asignado, ya ordenados de forma descendente por similitud.
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<PatronBusquedaResultado>>> buscar(
            @RequestParam(required = false) String textoMO,
            @RequestParam(required = false) String textoFirmaConductual,
            @RequestParam(required = false) Integer limite) {

        PatronBusquedaRequest request = new PatronBusquedaRequest(textoMO, textoFirmaConductual, limite);
        List<PatronBusquedaResultado> resultado = patronBusquedaService.buscar(request);

        String mensaje = resultado.isEmpty() ? "Sin resultados." : "Búsqueda de patrones completada.";
        return ResponseEntity.ok(ApiResponse.ok(mensaje, resultado));
    }
}
