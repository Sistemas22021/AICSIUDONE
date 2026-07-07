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

import java.util.List;

/** Adapter de entrada REST para suceso. Arma la respuesta anidando los nodos via readers. */
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

    public SucesoController(CreateSuceso createSuceso, ListSucesos listSucesos,
                            GetSuceso getSuceso, UpdateSuceso updateSuceso, DeleteSuceso deleteSuceso,
                            VehiculoReaderPort vehiculoReader, PersonaReaderPort personaReader,
                            UbicacionReaderPort ubicacionReader) {
        this.createSuceso = createSuceso;
        this.listSucesos = listSucesos;
        this.getSuceso = getSuceso;
        this.updateSuceso = updateSuceso;
        this.deleteSuceso = deleteSuceso;
        this.vehiculoReader = vehiculoReader;
        this.personaReader = personaReader;
        this.ubicacionReader = ubicacionReader;
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

    @GetMapping
    public List<SucesoResponse> listar(@RequestParam(required = false) TipoSuceso tipo) {
        return listSucesos.execute(tipo).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SucesoResponse obtener(@PathVariable Long id) {
        return toResponse(getSuceso.execute(id));
    }

    @PostMapping
    public ResponseEntity<SucesoResponse> crear(@Valid @RequestBody SucesoRequest req) {
        Suceso nuevo = Suceso.crear(
                req.getTipo(), req.getFechaHora(), req.getModusOperandi(), req.getDescripcion(),
                req.vehiculoId(), req.victimaId(), req.ubicacionId(), req.ubicacionUltimaId());
        return ResponseEntity.ok(toResponse(createSuceso.execute(nuevo)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deleteSuceso.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public SucesoResponse actualizar(@PathVariable Long id, @RequestBody SucesoRequest req) {
        Suceso actualizado = updateSuceso.execute(
                id, req.getTipo(), req.getFechaHora(),
                req.getModusOperandi(), req.getDescripcion());
        return toResponse(actualizado);
    }
}