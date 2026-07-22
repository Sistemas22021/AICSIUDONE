package com.nexocriminal.engine;

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
