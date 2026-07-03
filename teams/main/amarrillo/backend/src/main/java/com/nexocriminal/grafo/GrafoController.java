package com.nexocriminal.grafo;

import com.nexocriminal.domain.persona.Persona;
import com.nexocriminal.domain.persona.PersonaRepository;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.ubicacion.UbicacionRepository;
import com.nexocriminal.domain.vehiculo.Vehiculo;
import com.nexocriminal.domain.vehiculo.VehiculoRepository;
import com.nexocriminal.domain.suceso.Suceso;
import com.nexocriminal.domain.suceso.SucesoRepository;
import com.nexocriminal.domain.vinculo.Vinculo;
import com.nexocriminal.domain.vinculo.VinculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Entrega el grafo completo en formato listo para Cytoscape.js:
 * { nodes: [...], edges: [...] }
 */
@RestController
@RequestMapping("/api/v1/grafo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GrafoController {

    private final PersonaRepository personaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final UbicacionRepository ubicacionRepo;
    private final SucesoRepository sucesoRepo;
    private final VinculoRepository vinculoRepo;

    @GetMapping("/completo")
    public Map<String, Object> grafoCompleto() {
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        for (Persona p : personaRepo.findAll()) {
            nodes.add(Map.of(
                    "data", Map.of(
                            "id", "PERSONA_" + p.getId(),
                            "label", p.getNombre() + " " + p.getApellido(),
                            "tipo", "PERSONA",
                            "subtipo", p.getRol() != null ? p.getRol().name() : "N/A"
                    )
            ));
        }
        for (Vehiculo v : vehiculoRepo.findAll()) {
            nodes.add(Map.of(
                    "data", Map.of(
                            "id", "VEHICULO_" + v.getId(),
                            "label", v.getPlaca(),
                            "tipo", "VEHICULO",
                            "subtipo", v.getEstado() != null ? v.getEstado().name() : "NORMAL"
                    )
            ));
        }
        for (Ubicacion u : ubicacionRepo.findAll()) {
            nodes.add(Map.of(
                    "data", Map.of(
                            "id", "UBICACION_" + u.getId(),
                            "label", u.getDireccion() != null ? u.getDireccion() : ("Ubi #" + u.getId()),
                            "tipo", "UBICACION",
                            "subtipo", u.getTipo() != null ? u.getTipo().name() : "OTRO",
                            "sospechoso", Boolean.TRUE.equals(u.getNodoSospechoso())
                    )
            ));
        }
        for (Suceso s : sucesoRepo.findAll()) {
            nodes.add(Map.of(
                    "data", Map.of(
                            "id", "SUCESO_" + s.getId(),
                            "label", s.getTipo() + " #" + s.getId(),
                            "tipo", "SUCESO",
                            "subtipo", s.getTipo() != null ? s.getTipo().name() : "OTRO"
                    )
            ));
        }

        // Aristas desde la tabla vinculo (los hilos rojos)
        for (Vinculo v : vinculoRepo.findAllActivos()) {
            edges.add(Map.of(
                    "data", Map.of(
                            "id", "V_" + v.getId(),
                            "source", v.getTipoOrigen() + "_" + v.getIdOrigen(),
                            "target", v.getTipoDestino() + "_" + v.getIdDestino(),
                            "regla", v.getReglaDetectada(),
                            "score", v.getScore(),
                            "tipo", "HILO_ROJO"
                    )
            ));
        }

        // Aristas implícitas: vehículo -> propietario (persona)
        for (Vehiculo v : vehiculoRepo.findAll()) {
            if (v.getPropietario() != null) {
                edges.add(Map.of(
                        "data", Map.of(
                                "id", "PROP_" + v.getId(),
                                "source", "VEHICULO_" + v.getId(),
                                "target", "PERSONA_" + v.getPropietario().getId(),
                                "regla", "PROPIETARIO",
                                "tipo", "DIRECTO"
                        )
                ));
            }
        }

        return Map.of("nodes", nodes, "edges", edges);
    }

    @GetMapping("/nodo/{tipo}/{id}")
    public Map<String, Object> vinculosDelNodo(@PathVariable String tipo, @PathVariable Long id) {
        List<Vinculo> vinculos = vinculoRepo.findByNodo(tipo.toUpperCase(), id);
        return Map.of(
                "nodo", tipo.toUpperCase() + "_" + id,
                "vinculos", vinculos
        );
    }
}
