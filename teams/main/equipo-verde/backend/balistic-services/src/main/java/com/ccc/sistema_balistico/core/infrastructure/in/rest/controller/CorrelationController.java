package com.ccc.sistema_balistico.core.infrastructure.in.rest.controller;

import com.ccc.sistema_balistico.core.application.dto.CorrelationResultDTO;
import com.ccc.sistema_balistico.core.application.services.CorrelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/correlate")
@Tag(name = "Correlación Balística", description = "Endpoints para el análisis comparativo de proyectiles y generación de ranking de coincidencia")
public class CorrelationController {

    @Autowired
    private CorrelationService correlationService;

    @Operation(
            summary = "Correlacionar proyectil",
            description = "Realiza la comparación en cascada y análisis de similitud visual (OpenCV ORB + Lowe's Ratio Test + RANSAC) de un proyectil frente a otros del mismo calibre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Análisis completado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proyectil origen no encontrado")
    })
    @PostMapping("/{evidenceId}")
    public ResponseEntity<Page<CorrelationResultDTO>> correlate(
            @Parameter(description = "ID del proyectil origen a comparar")
            @PathVariable Long evidenceId,
            Pageable pageable) {
        Page<CorrelationResultDTO> page = correlationService.correlateBullet(evidenceId, pageable);
        return ResponseEntity.ok(page);
    }
}
