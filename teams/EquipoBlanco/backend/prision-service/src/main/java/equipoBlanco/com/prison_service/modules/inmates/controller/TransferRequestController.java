package equipoBlanco.com.prison_service.modules.inmates.controller;

import equipoBlanco.com.prison_service.modules.inmates.dto.TransferRequestDto;
import equipoBlanco.com.prison_service.modules.inmates.service.TransferRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransferRequestController {

    private final TransferRequestService transferRequestService;

    @PostMapping
    public ResponseEntity<TransferRequestDto> createRequest(
            @RequestBody TransferRequestDto dto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(transferRequestService.createRequest(dto, username));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<TransferRequestDto> resolveRequest(
            @PathVariable UUID id,
            @RequestBody TransferRequestDto resolveDto,
            @RequestHeader(value = "X-User-Name", defaultValue = "Oficial") String username) {
        return ResponseEntity.ok(transferRequestService.resolveRequest(id, resolveDto, username));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransferRequestDto>> getPending() {
        return ResponseEntity.ok(transferRequestService.getPendingRequests());
    }

    @GetMapping("/inmate/{inmateId}")
    public ResponseEntity<List<TransferRequestDto>> getInmateHistory(@PathVariable UUID inmateId) {
        return ResponseEntity.ok(transferRequestService.getInmateHistory(inmateId));
    }
}
