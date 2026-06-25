package com.ccc.sistema_balistico.core.infrastructure.in.rest.controller;

import com.ccc.sistema_balistico.core.application.dto.CaliberDTO;
import com.ccc.sistema_balistico.core.application.services.CaliberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/caliber")
@Tag(name = "Calibre", description = "Endpoints para la gestión y búsqueda de calibres balísticos")
@Validated
public class CaliberController {

    @Autowired
    private CaliberService caliberService;

    @Operation(
            summary = "Buscar calibres por nombre",
            description = "Retorna una página de hasta 10 calibres que coinciden con el texto de búsqueda y no están eliminados."
    )
    @GetMapping("/search")
    public ResponseEntity<Page<CaliberDTO>> search(
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(caliberService.searchCalibers(query));
    }
}
