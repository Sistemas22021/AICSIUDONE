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

import java.util.List;
import java.util.Map;

/** Adapter de entrada REST para vehiculo. Recibe DTOs, invoca casos de uso, devuelve DTOs. */
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

    @GetMapping
    public List<VehiculoResponse> listar(@RequestParam(required = false) EstadoVehiculo estado) {
        return listVehiculos.execute(estado).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public VehiculoResponse obtener(@PathVariable Long id) {
        return toResponse(getVehiculo.execute(id));
    }

    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(@Valid @RequestBody VehiculoRequest req) {
        Vehiculo nuevo = Vehiculo.crear(
                req.getPlaca(), req.getMarca(), req.getModelo(), req.getAnio(),
                req.getColor(), req.getEstado(), req.getPropietarioId());
        return ResponseEntity.ok(toResponse(createVehiculo.execute(nuevo)));
    }

    @PutMapping("/{id}")
    public VehiculoResponse actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoRequest req) {
        Vehiculo v = updateVehiculo.execute(
                id, req.getMarca(), req.getModelo(), req.getAnio(), req.getColor(),
                req.getEstado(), req.getPropietarioId());
        return toResponse(v);
    }

    @PatchMapping("/{id}/estado")
    public VehiculoResponse cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoVehiculo estado = EstadoVehiculo.valueOf(body.get("estado"));
        return toResponse(changeEstado.execute(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deleteVehiculo.execute(id);
        return ResponseEntity.noContent().build();
    }
}