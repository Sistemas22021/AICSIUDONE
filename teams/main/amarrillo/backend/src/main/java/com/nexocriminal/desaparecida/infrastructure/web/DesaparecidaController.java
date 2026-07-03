package com.nexocriminal.desaparecida.infrastructure.web;

import com.nexocriminal.desaparecida.application.*;
import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.desaparecida.domain.port.UbicacionReaderPort;
import com.nexocriminal.desaparecida.infrastructure.web.dto.DesaparecidaRequest;
import com.nexocriminal.desaparecida.infrastructure.web.dto.DesaparecidaResponse;
import com.nexocriminal.desaparecida.infrastructure.web.dto.DesaparecidaResponse.UbicacionNodo;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.FotoDesaparecida;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecidaService;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;
import com.nexocriminal.files.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Adapter de entrada REST para desaparecida. El CRUD usa casos de uso Clean;
 * el manejo de fotos y estadisticas se delega al PersonaDesaparecidaService viejo
 * (las fotos son archivos = infraestructura pura, no logica de dominio).
 */
@RestController
@RequestMapping("/api/v1/desaparecidas")
@CrossOrigin(origins = "*")
public class DesaparecidaController {

    private final CreateDesaparecida createDesaparecida;
    private final ListDesaparecidas listDesaparecidas;
    private final GetDesaparecida getDesaparecida;
    private final UpdateDesaparecida updateDesaparecida;
    private final ChangeDesaparecidaEstado changeEstado;
    private final DeleteDesaparecida deleteDesaparecida;
    private final BuscarCercanas buscarCercanas;
    private final UbicacionReaderPort ubicacionReader;
    // Service viejo: solo para fotos y estadisticas (infraestructura de archivos)
    private final PersonaDesaparecidaService fotoService;
    private final FileStorageService fileStorageService;

    public DesaparecidaController(CreateDesaparecida createDesaparecida, ListDesaparecidas listDesaparecidas,
                                  GetDesaparecida getDesaparecida, UpdateDesaparecida updateDesaparecida,
                                  ChangeDesaparecidaEstado changeEstado, DeleteDesaparecida deleteDesaparecida,
                                  BuscarCercanas buscarCercanas, UbicacionReaderPort ubicacionReader,
                                  PersonaDesaparecidaService fotoService, FileStorageService fileStorageService) {
        this.createDesaparecida = createDesaparecida;
        this.listDesaparecidas = listDesaparecidas;
        this.getDesaparecida = getDesaparecida;
        this.updateDesaparecida = updateDesaparecida;
        this.changeEstado = changeEstado;
        this.deleteDesaparecida = deleteDesaparecida;
        this.buscarCercanas = buscarCercanas;
        this.ubicacionReader = ubicacionReader;
        this.fotoService = fotoService;
        this.fileStorageService = fileStorageService;
    }

    private UbicacionNodo ubicacionNodo(Long id) {
        if (id == null) return null;
        return ubicacionReader.findById(id)
                .map(u -> new UbicacionNodo(u.id(), u.direccion(), u.latitud(), u.longitud()))
                .orElse(null);
    }

    /** Arma el response: dominio + fotos (de la entidad) + ubicacion anidada. */
    private DesaparecidaResponse toResponse(Desaparecida d) {
        List<FotoDesaparecida> fotos = fotoService.listarFotos(d.getId());
        return new DesaparecidaResponse(d, fotos, ubicacionNodo(d.getUltimaUbicacionId()));
    }

    @GetMapping
    public List<DesaparecidaResponse> listar(
            @RequestParam(required = false) EstadoDesaparicion estado,
            @RequestParam(required = false) PrioridadDesaparicion prioridad) {
        return listDesaparecidas.execute(estado, prioridad).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public DesaparecidaResponse obtener(@PathVariable Long id) {
        return toResponse(getDesaparecida.execute(id));
    }

    @PostMapping
    public ResponseEntity<DesaparecidaResponse> crear(@RequestBody DesaparecidaRequest req) {
        Desaparecida nueva = Desaparecida.crear(
                req.getDocumento(), req.getNombre(), req.getApellido(), req.getAlias(),
                req.getFechaNacimiento(), req.getGenero(), req.getEstaturaCm(), req.getPesoKg(),
                req.getContextura(), req.getColorCabello(), req.getColorOjos(), req.getSenasParticulares(),
                req.getRopaAlDesaparecer(), req.getFechaDesaparicion(), req.ultimaUbicacionId(),
                req.getCircunstancias(), req.getReportanteNombre(), req.getReportanteTelefono(),
                req.getReportanteRelacion(), req.getEstado(), req.getPrioridad());
        return ResponseEntity.ok(toResponse(createDesaparecida.execute(nueva)));
    }

    @PutMapping("/{id}")
    public DesaparecidaResponse actualizar(@PathVariable Long id, @RequestBody DesaparecidaRequest req) {
        Desaparecida d = updateDesaparecida.execute(
                id, req.getNombre(), req.getApellido(), req.getAlias(), req.getFechaNacimiento(),
                req.getGenero(), req.getEstaturaCm(), req.getPesoKg(), req.getContextura(),
                req.getColorCabello(), req.getColorOjos(), req.getSenasParticulares(),
                req.getRopaAlDesaparecer(), req.getFechaDesaparicion(), req.ultimaUbicacionId(),
                req.getCircunstancias(), req.getReportanteNombre(), req.getReportanteTelefono(),
                req.getReportanteRelacion(), req.getEstado(), req.getPrioridad());
        return toResponse(d);
    }

    @PatchMapping("/{id}/estado")
    public DesaparecidaResponse cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoDesaparicion estado = EstadoDesaparicion.valueOf(body.get("estado"));
        return toResponse(changeEstado.execute(id, estado));
    }

    @GetMapping("/cercanas")
    public List<DesaparecidaResponse> cercanas(
            @RequestParam double lat, @RequestParam double lng,
            @RequestParam(defaultValue = "5000") int radioMetros) {
        return buscarCercanas.execute(lat, lng, radioMetros).stream().map(this::toResponse).toList();
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> estadisticas() {
        List<Desaparecida> todas = listDesaparecidas.execute(null, null);
        long buscadas = todas.stream().filter(p -> p.getEstado() == EstadoDesaparicion.BUSCADA).count();
        long encontradasVivas = todas.stream().filter(p -> p.getEstado() == EstadoDesaparicion.ENCONTRADA_VIVA).count();
        long encontradasFallecidas = todas.stream().filter(p -> p.getEstado() == EstadoDesaparicion.ENCONTRADA_FALLECIDA).count();
        long criticas = todas.stream()
                .filter(p -> p.getEstado() == EstadoDesaparicion.BUSCADA)
                .filter(p -> p.getPrioridad() == PrioridadDesaparicion.CRITICA)
                .count();
        return Map.of(
                "total", todas.size(),
                "buscadas", buscadas,
                "encontradasVivas", encontradasVivas,
                "encontradasFallecidas", encontradasFallecidas,
                "criticas", criticas
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Borrar foto principal del disco si existe (via service viejo)
        try {
            Desaparecida d = getDesaparecida.execute(id);
            if (d.getFotoUrl() != null) {
                fileStorageService.eliminarArchivo(d.getFotoUrl());
            }
        } catch (Exception ignored) {}
        deleteDesaparecida.execute(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Fotos: delegadas al service viejo (infraestructura de archivos) =====

    @PostMapping("/{id}/foto")
    public ResponseEntity<Map<String, String>> subirFoto(@PathVariable Long id,
                                                         @RequestParam("archivo") MultipartFile archivo) {
        try {
            Desaparecida actual = getDesaparecida.execute(id);
            if (actual.getFotoUrl() != null) {
                fileStorageService.eliminarArchivo(actual.getFotoUrl());
            }
            String url = fileStorageService.guardarFotoDesaparecida(archivo);
            fotoService.actualizarFotoUrl(id, url);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al subir foto: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/fotos")
    public FotoDesaparecida agregarFoto(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        return fotoService.agregarFoto(id, archivo);
    }

    @GetMapping("/{id}/fotos")
    public List<FotoDesaparecida> listarFotos(@PathVariable Long id) {
        return fotoService.listarFotos(id);
    }

    @DeleteMapping("/{id}/fotos/{fotoId}")
    public Map<String, Boolean> eliminarFoto(@PathVariable Long id, @PathVariable Long fotoId) {
        fotoService.eliminarFoto(id, fotoId);
        return Map.of("eliminada", true);
    }

    @PatchMapping("/{id}/fotos/{fotoId}/principal")
    public Map<String, Boolean> marcarPrincipal(@PathVariable Long id, @PathVariable Long fotoId) {
        fotoService.marcarPrincipal(id, fotoId);
        return Map.of("actualizada", true);
    }
}