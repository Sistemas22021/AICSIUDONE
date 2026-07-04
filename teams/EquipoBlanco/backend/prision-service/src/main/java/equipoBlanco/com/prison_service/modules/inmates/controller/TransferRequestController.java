package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.TransferRequestDto;
import equipoBlanco.com.prison_service.modules.inmates.service.TransferRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Traslados (Transfers)", description = "Gestión de solicitudes de traslado de reclusos entre celdas")
public class TransferRequestController {

    private final TransferRequestService transferRequestService;

    @PostMapping
    @Operation(summary = "Crear solicitud de traslado", description = "Crea una nueva solicitud de traslado de un recluso a otra celda")
    public ResponseEntity<TransferRequestDto> createRequest(
            @RequestBody TransferRequestDto dto,
            @Parameter(description = "Nombre del oficial que solicita el traslado") @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(transferRequestService.createRequest(dto, username));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "Resolver solicitud de traslado", description = "Aprueba o rechaza una solicitud de traslado pendiente")
    public ResponseEntity<TransferRequestDto> resolveRequest(
            @Parameter(description = "UUID de la solicitud de traslado") @PathVariable UUID id,
            @RequestBody TransferRequestDto resolveDto,
            @Parameter(description = "Nombre del supervisor que resuelve la solicitud") @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(transferRequestService.resolveRequest(id, resolveDto, username));
    }

    @GetMapping("/pending")
    @Operation(summary = "Solicitudes pendientes", description = "Obtiene todas las solicitudes de traslado pendientes de resolución")
    public ResponseEntity<List<TransferRequestDto>> getPending() {
        return ResponseEntity.ok(transferRequestService.getPendingRequests());
    }

    @GetMapping("/inmate/{inmateId}")
    @Operation(summary = "Historial de traslados por recluso", description = "Obtiene el historial de solicitudes de traslado de un recluso específico")
    public ResponseEntity<List<TransferRequestDto>> getInmateHistory(
            @Parameter(description = "UUID del recluso") @PathVariable UUID inmateId) {
        return ResponseEntity.ok(transferRequestService.getInmateHistory(inmateId));
    }
}
