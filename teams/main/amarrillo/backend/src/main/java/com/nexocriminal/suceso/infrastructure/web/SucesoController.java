package com.nexocriminal.suceso.infrastructure.web;

import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.suceso.application.CreateSuceso;
import com.nexocriminal.suceso.application.DeleteSuceso;
import com.nexocriminal.suceso.application.GetSuceso;
import com.nexocriminal.suceso.application.ListSucesos;
import com.nexocriminal.suceso.application.UpdateSuceso;
import com.nexocriminal.suceso.domain.model.Suceso;
import com.nexocriminal.suceso.domain.port.PersonaReaderPort;
import com.nexocriminal.suceso.domain.port.UbicacionReaderPort;
import com.nexocriminal.suceso.domain.port.VehiculoReaderPort;
import com.nexocriminal.suceso.infrastructure.web.dto.SucesoRequest;
import com.nexocriminal.suceso.infrastructure.web.dto.SucesoResponse;
import com.nexocriminal.suceso.infrastructure.web.dto.SucesoResponse.UbicacionNodo;
import com.nexocriminal.suceso.infrastructure.web.dto.SucesoResponse.VehiculoNodo;
import com.nexocriminal.suceso.infrastructure.web.dto.SucesoResponse.VictimaNodo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.nexocriminal.integracion.CianClient;
import com.nexocriminal.ia.IAService;
import java.util.Map;
import com.nexocriminal.domain.ubicacion.Ubicacion;

import java.util.List;

/** Adapter de entrada REST para suceso. Arma la respuesta anidando los nodos via readers. */
@Tag(name = "Sucesos", description = "Registro de sucesos criminales (robos, avistamientos, transacciones)")
@RestController
@RequestMapping("/api/v1/sucesos")
@CrossOrigin(origins = "*")
public class SucesoController {

    private final CreateSuceso createSuceso;
    private final ListSucesos listSucesos;
    private final GetSuceso getSuceso;
    private final UpdateSuceso updateSuceso;
    private final DeleteSuceso deleteSuceso;
    private final VehiculoReaderPort vehiculoReader;
    private final PersonaReaderPort personaReader;
    private final UbicacionReaderPort ubicacionReader;
    private final CianClient cianClient;
    private final IAService iaService;

    public SucesoController(CreateSuceso createSuceso, ListSucesos listSucesos,
                            GetSuceso getSuceso, UpdateSuceso updateSuceso, DeleteSuceso deleteSuceso,
                            VehiculoReaderPort vehiculoReader, PersonaReaderPort personaReader,
                            UbicacionReaderPort ubicacionReader,
                            CianClient cianClient, IAService iaService) {
        this.createSuceso = createSuceso;
        this.listSucesos = listSucesos;
        this.getSuceso = getSuceso;
        this.updateSuceso = updateSuceso;
        this.deleteSuceso = deleteSuceso;
        this.vehiculoReader = vehiculoReader;
        this.personaReader = personaReader;
        this.ubicacionReader = ubicacionReader;
        this.cianClient = cianClient;
        this.iaService = iaService;
    }

    private VehiculoNodo vehiculoNodo(Long id) {
        if (id == null) return null;
        return vehiculoReader.findById(id)
                .map(v -> new VehiculoNodo(v.id(), v.placa(), v.marca(), v.modelo(), v.estado()))
                .orElse(null);
    }

    private VictimaNodo victimaNodo(Long id) {
        if (id == null) return null;
        return personaReader.findById(id)
                .map(p -> new VictimaNodo(p.id(), p.nombre(), p.apellido(), p.documento()))
                .orElse(null);
    }

    private UbicacionNodo ubicacionNodo(Long id) {
        if (id == null) return null;
        return ubicacionReader.findById(id)
                .map(u -> new UbicacionNodo(u.id(), u.direccion(), u.latitud(), u.longitud()))
                .orElse(null);
    }

    private SucesoResponse toResponse(Suceso s) {
        return new SucesoResponse(
                s,
                vehiculoNodo(s.getVehiculoId()),
                victimaNodo(s.getVictimaId()),
                ubicacionNodo(s.getUbicacionId()),
                ubicacionNodo(s.getUbicacionUltimaId())
        );
    }

    @Operation(summary = "Listar sucesos", description = "Devuelve todos los sucesos, opcionalmente filtrados por tipo")
    @GetMapping
    public List<SucesoResponse> listar(@RequestParam(required = false) TipoSuceso tipo) {
        return listSucesos.execute(tipo).stream().map(this::toResponse).toList();
    }

    @Operation(summary = "Obtener suceso", description = "Devuelve un suceso por su identificador, con sus entidades relacionadas")
    @GetMapping("/{id}")
    public SucesoResponse obtener(@PathVariable Long id) {
        return toResponse(getSuceso.execute(id));
    }

    @Operation(summary = "Crear suceso", description = "Registra un nuevo suceso vinculando vehículo, víctima y ubicación según el tipo")
    @PostMapping
    public ResponseEntity<SucesoResponse> crear(@Valid @RequestBody SucesoRequest req) {
        Suceso nuevo = Suceso.crear(
                req.getTipo(), req.getFechaHora(), req.getModusOperandi(), req.getDescripcion(),
                req.vehiculoId(), req.victimaId(), req.ubicacionId(), req.ubicacionUltimaId());
        return ResponseEntity.ok(toResponse(createSuceso.execute(nuevo)));
    }

    @Operation(summary = "Eliminar suceso", description = "Elimina un suceso del sistema por su identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deleteSuceso.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar suceso", description = "Modifica los campos simples de un suceso (fecha, modus operandi, descripción); las relaciones se mantienen")
    @PutMapping("/{id}")
    public SucesoResponse actualizar(@PathVariable Long id, @RequestBody SucesoRequest req) {
        Suceso actualizado = updateSuceso.execute(
                id, req.getTipo(), req.getFechaHora(),
                req.getModusOperandi(), req.getDescripcion());
        return toResponse(actualizado);
    }

    @Operation(summary = "Enviar suceso a patrullas (Cian)",
               description = "Calcula la prioridad con IA y envía el suceso como incidente al sistema de patrullaje del Equipo Cian")
    @PostMapping("/{id}/enviar-cian")
    public ResponseEntity<?> enviarACian(@PathVariable Long id) {
        Suceso s = getSuceso.execute(id);

        // Resolver la ubicación del hecho vía el reader (el dominio solo tiene el id)
        Long ubicacionId = s.getUbicacionId();
        if (ubicacionId == null) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "El suceso no tiene ubicación; no se puede enviar a patrullas."));
        }
        var ubiOpt = ubicacionReader.findById(ubicacionId);
        if (ubiOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "La ubicación del suceso no tiene coordenadas; no se puede enviar a patrullas."));
        }
        var ubi = ubiOpt.get();

        try {
            String priority = iaService.calcularPrioridadIncidente(id);
            String type = s.getTipo() != null ? s.getTipo().name() : "OTRO";
            String description = s.getDescripcion() != null ? s.getDescripcion() : s.getModusOperandi();

            Map<String, Object> respuestaCian = cianClient.enviarIncidente(
                    type, description, ubi.latitud(), ubi.longitud(), priority);

            return ResponseEntity.ok(Map.of("prioridad", priority, "incidenteCian", respuestaCian));
        } catch (org.springframework.web.client.ResourceAccessException e) {
            return ResponseEntity.status(503).body(
                Map.of("error", "No se pudo conectar con el sistema de patrullas (Cian). ¿Está corriendo?"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("error", "Error al enviar el incidente: " + e.getMessage()));
        }
    }
}