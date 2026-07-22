package com.nexocriminal.vehiculo.infrastructure.web;

import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.vehiculo.application.*;
import com.nexocriminal.vehiculo.domain.model.Vehiculo;
import com.nexocriminal.vehiculo.domain.port.PersonaReaderPort;
import com.nexocriminal.vehiculo.infrastructure.web.dto.PropietarioResumen;
import com.nexocriminal.vehiculo.infrastructure.web.dto.VehiculoRequest;
import com.nexocriminal.vehiculo.infrastructure.web.dto.VehiculoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

/** Adapter de entrada REST para vehiculo. Recibe DTOs, invoca casos de uso, devuelve DTOs. */
@Tag(name = "Vehículos", description = "Gestión de vehículos y su estado (normal, robado, recuperado)")
@RestController
@RequestMapping("/api/v1/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private final CreateVehiculo createVehiculo;
    private final ListVehiculos listVehiculos;
    private final GetVehiculo getVehiculo;
    private final UpdateVehiculo updateVehiculo;
    private final ChangeVehiculoEstado changeEstado;
    private final DeleteVehiculo deleteVehiculo;
    private final PersonaReaderPort personaReader;

    public VehiculoController(CreateVehiculo createVehiculo, ListVehiculos listVehiculos,
                              GetVehiculo getVehiculo, UpdateVehiculo updateVehiculo,
                              ChangeVehiculoEstado changeEstado, DeleteVehiculo deleteVehiculo,
                              PersonaReaderPort personaReader) {
        this.createVehiculo = createVehiculo;
        this.listVehiculos = listVehiculos;
        this.getVehiculo = getVehiculo;
        this.updateVehiculo = updateVehiculo;
        this.changeEstado = changeEstado;
        this.deleteVehiculo = deleteVehiculo;
        this.personaReader = personaReader;
    }

    /** Construye el response anidando el propietario (si tiene). */
    private VehiculoResponse toResponse(Vehiculo v) {
        PropietarioResumen prop = null;
        if (v.getPropietarioId() != null) {
            prop = personaReader.findById(v.getPropietarioId())
                    .map(PropietarioResumen::new)
                    .orElse(null);
        }
        return new VehiculoResponse(v, prop);
    }

    @Operation(summary = "Listar vehículos", description = "Devuelve todos los vehículos, opcionalmente filtrados por estado")
    @GetMapping
    public List<VehiculoResponse> listar(@RequestParam(required = false) EstadoVehiculo estado) {
        return listVehiculos.execute(estado).stream().map(this::toResponse).toList();
    }

    @Operation(summary = "Obtener vehículo", description = "Devuelve un vehículo por su identificador, incluyendo su propietario")
    @GetMapping("/{id}")
    public VehiculoResponse obtener(@PathVariable Long id) {
        return toResponse(getVehiculo.execute(id));
    }

    @Operation(summary = "Crear vehículo", description = "Registra un nuevo vehículo con sus datos y propietario opcional")
    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(@Valid @RequestBody VehiculoRequest req) {
        Vehiculo nuevo = Vehiculo.crear(
                req.getPlaca(), req.getMarca(), req.getModelo(), req.getAnio(),
                req.getColor(), req.getEstado(), req.getPropietarioId());
        return ResponseEntity.ok(toResponse(createVehiculo.execute(nuevo)));
    }

    @Operation(summary = "Actualizar vehículo", description = "Modifica los datos de un vehículo existente")
    @PutMapping("/{id}")
    public VehiculoResponse actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoRequest req) {
        Vehiculo v = updateVehiculo.execute(
                id, req.getMarca(), req.getModelo(), req.getAnio(), req.getColor(),
                req.getEstado(), req.getPropietarioId());
        return toResponse(v);
    }

    @Operation(summary = "Cambiar estado del vehículo", description = "Cambia el estado del vehículo (por ejemplo, marcarlo como robado o recuperado)")
    @PatchMapping("/{id}/estado")
    public VehiculoResponse cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoVehiculo estado = EstadoVehiculo.valueOf(body.get("estado"));
        return toResponse(changeEstado.execute(id, estado));
    }

    @Operation(summary = "Eliminar vehículo", description = "Elimina un vehículo del sistema por su identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deleteVehiculo.execute(id);
        return ResponseEntity.noContent().build();
    }
}