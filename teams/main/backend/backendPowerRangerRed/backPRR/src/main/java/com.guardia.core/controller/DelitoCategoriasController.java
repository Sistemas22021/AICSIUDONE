package com.guardia.core.controller;

import com.guardia.core.dto.response.TipoDelitoResponse;
import com.guardia.core.dto.response.SubtipoDelitoResponse;
import com.guardia.core.service.TipoDelitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador: expone /api/v1/delitos/categorias con el formato que espera el frontend.
 *
 * El frontend (useDelitoCategories.ts) espera:
 *   [ { "value": "...", "label": "...", "subtipos": [ { "value": "...", "label": "..." } ] } ]
 *
 * El servicio TipoDelitoService devuelve:
 *   [ { "id": 1, "nombre": "...", "descripcion": "...", "requiereSubtipo": true,
 *       "subtipos": [ { "id": 1, "nombre": "...", "tipoDelitoId": 1, ... } ] } ]
 *
 * Este controller transforma el formato del servicio al del frontend.
 */
@RestController
@RequestMapping("/api/v1/delitos")
@RequiredArgsConstructor
public class DelitoCategoriasController {

    private final TipoDelitoService tipoDelitoService;

    /**
     * GET /api/v1/delitos/categorias
     * Devuelve todos los tipos de delito con sus subtipos en formato compatible con el frontend.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<Map<String, Object>>> obtenerCategorias() {

        List<TipoDelitoResponse> tipos = tipoDelitoService.obtenerTodos();

        List<Map<String, Object>> resultado = tipos.stream()
                .map(tipo -> {
                    List<Map<String, Object>> subtipos = tipo.subtipos() == null
                            ? List.of()
                            : tipo.subtipos().stream()
                                    .map(sub -> Map.<String, Object>of(
                                            "value", slugify(sub.nombre()),
                                            "label", sub.nombre()
                                    ))
                                    .collect(Collectors.toList());

                    return Map.<String, Object>of(
                            "value",    slugify(tipo.nombre()),
                            "label",    tipo.nombre().toUpperCase(),
                            "subtipos", subtipos
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    /**
     * Convierte un nombre legible en un value tipo slug.
     * Ejemplo: "Homicidio Calificado" → "homicidio_calificado"
     */
    private String slugify(String nombre) {
        if (nombre == null) return "";
        return nombre.trim()
                .toLowerCase()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("[ñ]", "n")
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");
    }
}
