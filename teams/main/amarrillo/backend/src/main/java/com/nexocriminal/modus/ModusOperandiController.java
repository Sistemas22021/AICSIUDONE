package com.nexocriminal.modus;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Modus Operandi", description = "Catálogo de modus operandi para clasificar sucesos")
@RestController
@RequestMapping("/api/v1/modus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ModusOperandiController {

    private final ModusOperandiService service;

    /** Para el dropdown: solo los activos. */
    @GetMapping
    public List<ModusOperandi> listar() {
        return service.listarActivos();
    }

    /** Para la pantalla de gestión: todos, incluso inactivos. */
    @GetMapping("/todos")
    public List<ModusOperandi> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public ModusOperandi crear(@RequestBody ModusOperandi m) {
        return service.crear(m);
    }

    @PutMapping("/{id}")
    public ModusOperandi actualizar(@PathVariable Long id, @RequestBody ModusOperandi m) {
        return service.actualizar(id, m);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}package com.nexocriminal.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@Tag(name = "Motor Red Thread", description = "Ejecución de las reglas heurísticas de detección de vínculos ocultos")
@RestController
@RequestMapping("/api/v1/engine")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EngineController {

    private final RedThreadEngineService engineService;

    @PostMapping("/ejecutar-todo")
    public Map<String, Object> ejecutarTodo() {
        List<ReglaVinculo.ResultadoRegla> resultados = engineService.ejecutarTodas();
        int totalVinculos = resultados.stream().mapToInt(r -> r.getVinculosDetectados().size()).sum();
        int totalAlertas = resultados.stream().mapToInt(r -> r.getAlertasGeneradas().size()).sum();
        return Map.of(
                "reglasEjecutadas", resultados.size(),
                "totalVinculos", totalVinculos,
                "totalAlertas", totalAlertas,
                "detalle", resultados.stream().map(r -> Map.of(
                        "regla", r.getReglaNombre(),
                        "vinculos", r.getVinculosDetectados().size(),
                        "alertas", r.getAlertasGeneradas().size()
                )).toList()
        );
    }

    @PostMapping("/nodo-logistico")
    public ReglaVinculo.ResultadoRegla ejecutarNodoLogistico() {
        return engineService.ejecutarNodoLogistico();
    }

    @PostMapping("/escolta")
    public ReglaVinculo.ResultadoRegla ejecutarEscolta() {
        return engineService.ejecutarEscolta();
    }

    @PostMapping("/circulo-confianza")
    public ReglaVinculo.ResultadoRegla ejecutarCirculoConfianza() {
        return engineService.ejecutarCirculoConfianza();
    }

    @PostMapping("/modus-operandi")
    public ReglaVinculo.ResultadoRegla ejecutarModusOperandi() {
        return engineService.ejecutarModusOperandi();
    }

    @PostMapping("/cluster-desapariciones")
    public ReglaVinculo.ResultadoRegla ejecutarClusterDesapariciones() {
        return engineService.ejecutarClusterDesapariciones();
    }   
}