package com.nexocriminal.ia;

import com.nexocriminal.ia.dto.ChatRequest;
import com.nexocriminal.ia.dto.MensajeChat;
import com.nexocriminal.ia.dto.RespuestaIA;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Inteligencia Artificial", description = "Chat, análisis de zonas de búsqueda, reportes y clasificación de modus con IA")
@RestController
@RequestMapping("/api/v1/ia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IAController {

    private final IAService iaService;
    private final ClaudeClient claudeClient;

    @GetMapping("/estado")
    public Map<String, Object> estado() {
        return Map.of(
                "configurada", claudeClient.configurada(),
                "modelo", "claude-sonnet-4-5-20250929"
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest req) {
        try {
            List<MensajeChat> historial = req.getHistorial() != null
                    ? new ArrayList<>(req.getHistorial())
                    : new ArrayList<>();
            RespuestaIA r = iaService.chat(historial, req.getPregunta(), req.isIncluirContexto());
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/zonas-busqueda/{id}")
    public ResponseEntity<?> zonasBusqueda(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(iaService.predecirZonasBusqueda(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/similitud-desapariciones")
    public ResponseEntity<?> similitud() {
        try {
            return ResponseEntity.ok(iaService.analizarSimilitudDesapariciones());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reporte/{tipo}/{id}")
    public ResponseEntity<?> reporte(@PathVariable String tipo, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(iaService.generarReporte(tipo, id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/clasificar-modus")
    public ResponseEntity<?> clasificarModus(@RequestBody Map<String, String> body) {
        try {
            String descripcion = body.get("descripcion");
            return ResponseEntity.ok(iaService.clasificarModus(descripcion));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}