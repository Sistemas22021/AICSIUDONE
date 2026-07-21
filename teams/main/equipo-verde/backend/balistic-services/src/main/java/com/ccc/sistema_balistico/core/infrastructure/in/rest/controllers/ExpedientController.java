package com.ccc.sistema_balistico.core.infrastructure.in.rest.controllers;

import com.ccc.sistema_balistico.core.application.dto.ExpedientDTO;
import com.ccc.sistema_balistico.core.application.services.ExpedientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expedients")
@CrossOrigin(origins = "http://localhost:3002")
@Tag(name = "Gestión de Expedientes", description = "Endpoints para la administración y consulta de expedientes forenses")
public class ExpedientController {

    private final ExpedientService expedientService;

    @Autowired
    public ExpedientController(ExpedientService expedientService) {
        this.expedientService = expedientService;
    }

    @Operation(
            summary = "Registrar nuevo expediente",
            description = "Crea un nuevo registro de expediente en la base de datos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expediente creado exitosamente")
    })
    @PostMapping
    public ResponseEntity<ExpedientDTO> createExpedient(
            @Parameter(description = "Datos del expediente a crear") 
            @RequestBody ExpedientDTO expedientDTO) {
        ExpedientDTO created = expedientService.createExpedient(expedientDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar expediente existente",
            description = "Modifica los datos (número, descripción, estado) de un expediente previamente registrado."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ExpedientDTO> updateExpedient(
            @Parameter(description = "ID único del expediente", example = "1") @PathVariable("id") Long id, 
            @RequestBody ExpedientDTO expedientDTO) {
        ExpedientDTO updated = expedientService.updateExpedient(id, expedientDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @Operation(
            summary = "Buscar expediente por ID",
            description = "Recupera los detalles de un expediente usando su ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExpedientDTO> getExpedientById(
            @Parameter(description = "ID único del expediente", example = "1") @PathVariable("id") Long id) {
        ExpedientDTO dto = expedientService.getExpedientById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(
            summary = "Obtener expedientes paginados",
            description = "Retorna una lista paginada de expedientes, filtrados opcionalmente por una palabra clave."
    )
    @GetMapping
    public ResponseEntity<Page<ExpedientDTO>> getAllExpedients(
            @Parameter(description = "Palabra clave para buscar por código o descripción") @RequestParam(required = false, defaultValue = "") String keyword,
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página") @RequestParam(defaultValue = "10") int size) {
        Page<ExpedientDTO> expedients = expedientService.getAllExpedients(keyword, PageRequest.of(page, size));
        return new ResponseEntity<>(expedients, HttpStatus.OK);
    }

    @Operation(
            summary = "Eliminar (Borrado lógico) de expediente",
            description = "Marca un expediente como eliminado en el sistema sin borrarlo físicamente."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpedient(
            @Parameter(description = "ID único del expediente a eliminar", example = "1") @PathVariable("id") Long id) {
        expedientService.deleteExpedient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
