package com.nexocriminal.ubicacion.infrastructure.web;

import com.nexocriminal.ubicacion.application.CreateUbicacion;
import com.nexocriminal.ubicacion.application.DeleteUbicacion;
import com.nexocriminal.ubicacion.application.GetUbicacion;
import com.nexocriminal.ubicacion.application.ListUbicaciones;
import com.nexocriminal.ubicacion.application.UpdateUbicacion;
import com.nexocriminal.ubicacion.domain.model.Ubicacion;
import com.nexocriminal.ubicacion.infrastructure.web.dto.UbicacionRequest;
import com.nexocriminal.ubicacion.infrastructure.web.dto.UbicacionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adapter de entrada (REST). Recibe DTOs, invoca casos de uso y devuelve DTOs.
 * No conoce JPA ni la entidad de persistencia.
 */
@RestController
@RequestMapping("/api/v1/ubicaciones")
@CrossOrigin(origins = "*")
public class UbicacionController {

    private final CreateUbicacion createUbicacion;
    private final ListUbicaciones listUbicaciones;
    private final GetUbicacion getUbicacion;
    private final UpdateUbicacion updateUbicacion;
    private final DeleteUbicacion deleteUbicacion;

    public UbicacionController(CreateUbicacion createUbicacion,
                               ListUbicaciones listUbicaciones,
                               GetUbicacion getUbicacion,
                               UpdateUbicacion updateUbicacion,
                               DeleteUbicacion deleteUbicacion) {
        this.createUbicacion = createUbicacion;
        this.listUbicaciones = listUbicaciones;
        this.getUbicacion = getUbicacion;
        this.updateUbicacion = updateUbicacion;
        this.deleteUbicacion = deleteUbicacion;
    }

    @GetMapping
    public List<UbicacionResponse> listar() {
        return listUbicaciones.execute().stream()
                .map(UbicacionResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public UbicacionResponse obtener(@PathVariable Long id) {
        return new UbicacionResponse(getUbicacion.execute(id));
    }

    @PostMapping
    public ResponseEntity<UbicacionResponse> crear(@Valid @RequestBody UbicacionRequest req) {
        Ubicacion nueva = Ubicacion.crear(
                req.getDireccion(), req.getLatitud(), req.getLongitud(), req.getTipo());
        Ubicacion creada = createUbicacion.execute(nueva);
        return ResponseEntity.ok(new UbicacionResponse(creada));
    }

    @PutMapping("/{id}")
    public UbicacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody UbicacionRequest req) {
        Ubicacion actualizada = updateUbicacion.execute(
                id, req.getDireccion(), req.getLatitud(), req.getLongitud(), req.getTipo());
        return new UbicacionResponse(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deleteUbicacion.execute(id);
        return ResponseEntity.noContent().build();
    }
}